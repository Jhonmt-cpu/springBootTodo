package com.todo.spring.modules.todos.services;

import com.todo.spring.modules.todos.dtos.CreateRemoteTodoDTO;
import com.todo.spring.modules.todos.dtos.CreateTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateRemoteTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateTodoDTO;
import com.todo.spring.modules.todos.models.Todo;
import com.todo.spring.modules.todos.repositories.TodoRepository;
import com.todo.spring.modules.todos.repositories.TodoTypesRepository;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import com.todo.spring.shared.utils.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TodoTypesRepository todoTypesRepository;

    @Resource
    private RestService restService;

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

            HttpEntity<CreateRemoteTodoDTO> entity = new HttpEntity<>(remoteTodo, restService.getDefaultHeaders());

            try {
                ResponseEntity<Todo> response = restService.getRestTemplate().postForEntity(
                        restService.getBaseUrl() + "/todo",
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
            HttpEntity entity = new HttpEntity<>(restService.getDefaultHeaders());

            try {
                ResponseEntity<Todo> response = this.restService.getRestTemplate().exchange(
                        restService.getBaseUrl() + "/todo/" + todoId,
                        HttpMethod.GET,
                        entity,
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
            HttpEntity entity = new HttpEntity<>(restService.getDefaultHeaders());

            try {
                ResponseEntity<UpdateRemoteTodoDTO> response = this.restService.getRestTemplate().exchange(
                        restService.getBaseUrl() + "/todo/" + todoId,
                        HttpMethod.GET,
                        entity,
                        UpdateRemoteTodoDTO.class
                );

                UpdateRemoteTodoDTO todoForUpdate = response.getBody();

                assert todoForUpdate != null;
                todoForUpdate.setTitle(todo.getTitle());
                todoForUpdate.setDescription(todo.getDescription());
                todoForUpdate.setTypeId(todo.getTypeId());
                todoForUpdate.setUpdatedAt(LocalDateTime.now().toString());

                HttpEntity<UpdateRemoteTodoDTO> putEntity = new HttpEntity<>(todoForUpdate, restService.getDefaultHeaders());

                try {
                    ResponseEntity<Todo> putResponse = restService.getRestTemplate().exchange(
                            restService.getBaseUrl() + "/todo",
                            HttpMethod.PUT,
                            putEntity,
                            Todo.class
                    );

                    return putResponse.getBody();
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

    public void delete(UUID id) {
        boolean checkTodoExists = todoRepository.existsById(id);

        if (!checkTodoExists) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Todo doesn't exists"
            );
        }

        todoRepository.deleteById(id);
    }

    public List<Todo> list(String title) {
        if (title != null) {
            return todoRepository.findByTitleContainingIgnoreCase(title);
        } else {
            return todoRepository.findAll();
        }
    }
}
