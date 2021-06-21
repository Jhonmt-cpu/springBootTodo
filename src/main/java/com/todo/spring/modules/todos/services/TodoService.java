package com.todo.spring.modules.todos.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.spring.modules.todos.dtos.*;
import com.todo.spring.modules.todos.models.Todo;
import com.todo.spring.modules.todos.repositories.TodoRepository;
import com.todo.spring.modules.todos.repositories.TodoTypesRepository;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import com.todo.spring.shared.utils.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TodoTypesRepository todoTypesRepository;

    @Autowired
    @Qualifier("RemoteTodoClient")
    private RestTemplate restTemplate;

    public Todo create(CreateTodoDTO createTodoDTO) {
        User checkUserExists = usersRepository.findById(createTodoDTO.getUserId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User not found"
        ));

        boolean checkTodoTypeExists = todoTypesRepository.existsById(createTodoDTO.getTypeId());

        if (!checkTodoTypeExists) {
            throw  new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Todo type doesn't exists"
            );
        }

        if (checkUserExists.getTypeId() == 2) {
            CreateRemoteTodoDTO remoteTodo = CreateRemoteTodoDTO.builder()
                    .title(createTodoDTO.getTitle())
                    .description(createTodoDTO.getDescription())
                    .localOwner("Jão")
                    .userId(createTodoDTO.getUserId())
                    .typeId(createTodoDTO.getTypeId())
                    .updatedAt(LocalDateTime.now().toString())
                    .build();

            HttpEntity<CreateRemoteTodoDTO> entity = new HttpEntity<CreateRemoteTodoDTO>(remoteTodo);

            try {
                ResponseEntity<Todo> response = restTemplate.postForEntity(
                        restTemplate + "/todo",
                        entity,
                        Todo.class
                );

                return response.getBody();
            } catch (HttpClientErrorException error) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Failed to register the todo, try again later"
                );
            }
        }

        Todo todo = Todo.builder()
                .title(createTodoDTO.getTitle())
                .description(createTodoDTO.getDescription())
                .typeId(createTodoDTO.getTypeId())
                .userId(createTodoDTO.getUserId())
                .build();

        return todoRepository.save(todo);
    }

    public Todo show(UUID todoId, UUID userId) {
        User checkUserExists = usersRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User doesn't exists"
        ));

        if (checkUserExists.getTypeId() == 2) {
            try {
                ResponseEntity<Todo> response = this.restTemplate.getForEntity(
                        "/todo/" + todoId,
                        Todo.class
                );

                return response.getBody();
            } catch (HttpClientErrorException error) {
                return todoRepository.findById(todoId).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Todo doesn't exists"
                ));
            }
        }

        return todoRepository.findById(todoId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Todo doesn't exists"
        ));
    }

    public Todo update(UUID userId ,UUID todoId, UpdateTodoDTO todo) {
        User checkUserExists = usersRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User doesn't exists"
                )
        );

        boolean checkTodoTypeExists = todoTypesRepository.existsById(todo.getTypeId());

        if (!checkTodoTypeExists) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Todo type doesn't exists"
            );
        }

        if (checkUserExists.getTypeId() == 2) {
            try {
                ResponseEntity<UpdateRemoteTodoDTO> response = this.restTemplate.getForEntity(
                        "/todo/" + todoId,
                        UpdateRemoteTodoDTO.class
                );

                UpdateRemoteTodoDTO todoForUpdate = response.getBody();

                assert todoForUpdate != null;
                todoForUpdate.setTitle(todo.getTitle());
                todoForUpdate.setDescription(todo.getDescription());
                todoForUpdate.setTypeId(todo.getTypeId());
                todoForUpdate.setUpdatedAt(LocalDateTime.now().toString());

                HttpEntity<UpdateRemoteTodoDTO> putEntity = new HttpEntity<>(todoForUpdate);

                try {
                    restTemplate.put(
                            "/todo",
                            putEntity
                    );

                    return Todo.builder()
                            .title(todoForUpdate.getTitle())
                            .id(todoForUpdate.getId())
                            .typeId(todoForUpdate.getTypeId())
                            .description(todoForUpdate.getDescription())
                            .userId(todoForUpdate.getUserId())
                            .createdAt(LocalDateTime.parse(todoForUpdate.getCreatedAt()))
                            .updatedAt(LocalDateTime.parse(todoForUpdate.getUpdatedAt()))
                            .build();
                } catch (HttpClientErrorException error) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Failed to update the todo, try again later"
                    );
                }
            } catch (HttpClientErrorException error) {
                Todo checkLocalTodoExists = todoRepository.findById(todoId).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Todo doesn't exists"
                ));

                checkLocalTodoExists.setTitle(todo.getTitle());
                checkLocalTodoExists.setDescription(todo.getDescription());
                checkLocalTodoExists.setTypeId(todo.getTypeId());

                return todoRepository.save(checkLocalTodoExists);
            }
        }

        Todo checkLocalTodoExists = todoRepository.findById(todoId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Todo doesn't exists"
        ));

        checkLocalTodoExists.setTitle(todo.getTitle());
        checkLocalTodoExists.setDescription(todo.getDescription());
        checkLocalTodoExists.setTypeId(todo.getTypeId());

        return todoRepository.save(checkLocalTodoExists);
    }

    public void delete(UUID userId ,UUID todoId) {
        User checkUserExists = usersRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User doesn't exists"
                )
        );

        if (checkUserExists.getTypeId() == 2) {
            try {
                ResponseEntity<Todo> response = this.restTemplate.getForEntity(
                        "/todo/" + todoId,
                        Todo.class
                );

                UUID remoteTodoId = Objects.requireNonNull(response.getBody()).getId();

                try {
                    restTemplate.delete(
                            "/todo/" + remoteTodoId
                    );
                } catch (HttpClientErrorException error) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Failed to delete the todo, try again later"
                    );
                }
            } catch (HttpClientErrorException error) {
                Todo checkLocalTodoExists = todoRepository.findById(todoId).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Todo doesn't exists"
                ));

                todoRepository.delete(checkLocalTodoExists);
            }

        }
        boolean checkTodoExists = todoRepository.existsById(todoId);

        if (!checkTodoExists) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Todo doesn't exists"
            );
        }

        todoRepository.deleteById(todoId);
    }

    public List<Todo> list(UUID userId ,String title) {
        User checkUserExists = usersRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User doesn't exists"
                )
        );

        if (checkUserExists.getTypeId() == 2) {
            try {
                ResponseEntity<GetRemoteTodo[]> response = restTemplate.getForEntity(
                        "/todo",
                        GetRemoteTodo[].class
                );

                GetRemoteTodo[] remoteTodos = response.getBody();

                assert remoteTodos != null;
                List<GetRemoteTodo> jaoRemotesTodos = Arrays.stream(remoteTodos)
                        .filter(todo -> "Jão".equals(todo.getLocalOwner()))
                        .collect(Collectors.toList());

                List<Todo> todos = jaoRemotesTodos.stream().map(
                        jaoTD -> Todo.builder()
                                .title(jaoTD.getTitle())
                                .id(UUID.fromString(jaoTD.getId()))
                                .description(jaoTD.getDescription())
                                .userId(UUID.fromString(jaoTD.getUserId()))
                                .typeId(jaoTD.getTypeId())
                                .createdAt(jaoTD.getCreatedAt())
                                .updatedAt(jaoTD.getUpdatedAt())
                                .build()
                ).collect(Collectors.toList());

                if (title == null) {
                    todos.addAll(todoRepository.findAll());
                    return todos;
                }

                List<Todo> todosWithTitle = todos.stream()
                        .filter(todo -> todo.getTitle().contains(title))
                        .collect(Collectors.toList());

                todosWithTitle.addAll(todoRepository.findByTitleContainingIgnoreCase(title));
                return todos;
            } catch (HttpClientErrorException error) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Failed to get the todo list, try again later"
                );
            }
        }
        if (title != null) {
            return todoRepository.findByTitleContainingIgnoreCase(title);
        } else {
            return todoRepository.findAll();
        }
    }
}
