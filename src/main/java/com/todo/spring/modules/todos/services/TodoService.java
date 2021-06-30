package com.todo.spring.modules.todos.services;

import com.todo.spring.modules.todos.dtos.*;
import com.todo.spring.modules.todos.exceptions.*;
import com.todo.spring.modules.todos.models.Todo;
import com.todo.spring.modules.todos.repositories.RemoteTodoRepository;
import com.todo.spring.modules.todos.repositories.TodoRepository;
import com.todo.spring.modules.todos.repositories.TodoTypesRepository;
import com.todo.spring.modules.users.dtos.UserAuthenticatedDTO;
import com.todo.spring.modules.users.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoTypesRepository todoTypesRepository;

    @Autowired
    private RemoteTodoRepository remoteTodoRepository;

    public Todo create(UserAuthenticatedDTO userAuthenticated, CreateTodoDTO createTodoDTO) {
        boolean checkTodoTypeExists = todoTypesRepository.existsById(createTodoDTO.getTypeId());

        if (!checkTodoTypeExists) {
            throw new TodoTypeNotFoundException();
        }

        if (userAuthenticated.getTypeId() >= 2) {
            CreateRemoteTodoDTO remoteTodo = CreateRemoteTodoDTO.builder()
                    .title(createTodoDTO.getTitle())
                    .description(createTodoDTO.getDescription())
                    .localOwner("Jão")
                    .userId(userAuthenticated.getId())
                    .typeId(createTodoDTO.getTypeId())
                    .updatedAt(LocalDateTime.now().toString())
                    .build();

            try {
                return remoteTodoRepository.create(remoteTodo);
            } catch (HttpClientErrorException error) {
                throw new FailedToRegisterRemoteTodoException();
            }
        }

        Todo todo = Todo.builder()
                .title(createTodoDTO.getTitle())
                .description(createTodoDTO.getDescription())
                .typeId(createTodoDTO.getTypeId())
                .userId(userAuthenticated.getId())
                .build();

        return todoRepository.save(todo);
    }

    public Todo show(UserAuthenticatedDTO userAuthenticated, UUID todoId) {
        if (userAuthenticated.getTypeId() >= 2) {
            try {
                return remoteTodoRepository.getById(todoId);
            } catch (HttpClientErrorException error) {
                Optional<Todo> todo = todoRepository.findById(todoId);

                if (todo.isEmpty()) {
                    throw new TodoNotFoundException();
                }

                return todo.get();
            }
        }

        Optional<Todo> todo = todoRepository.findById(todoId);

        if (todo.isEmpty()) {
            throw new TodoNotFoundException();
        }

        return todo.get();
    }

    public Todo update(UserAuthenticatedDTO userAuthenticated ,UUID todoId, UpdateTodoDTO todo) {
        boolean checkTodoTypeExists = todoTypesRepository.existsById(todo.getTypeId());

        if (!checkTodoTypeExists) {
            throw new TodoTypeNotFoundException();
        }

        if (userAuthenticated.getTypeId() >= 2) {
            try {
                UpdateRemoteTodoDTO todoForUpdate = remoteTodoRepository.getByIdForUpdate(todoId);

                todoForUpdate.setTitle(todo.getTitle());
                todoForUpdate.setDescription(todo.getDescription());
                todoForUpdate.setTypeId(todo.getTypeId());
                todoForUpdate.setUpdatedAt(LocalDateTime.now().toString());

                try {
                    remoteTodoRepository.update(todoForUpdate);

                    return Todo.builder()
                            .title(todoForUpdate.getTitle())
                            .id(todoForUpdate.getId())
                            .typeId(todoForUpdate.getTypeId())
                            .description(todoForUpdate.getDescription())
                            .userId(todoForUpdate.getUserId())
                            .createdAt(LocalDateTime.parse(
                                    todoForUpdate.getCreatedAt()
                                            .substring(0, todoForUpdate.getCreatedAt().length() - 1)
                                    )
                            )
                            .updatedAt(LocalDateTime.parse(todoForUpdate.getUpdatedAt()))
                            .build();
                } catch (HttpClientErrorException error) {
                    throw new FailedToUpdateRemoteTodoException();
                }
            } catch (HttpClientErrorException error) {
                Optional<Todo> checkLocalTodoExists = todoRepository.findById(todoId);

                if (checkLocalTodoExists.isEmpty()) {
                    throw new TodoNotFoundException();
                }

                Todo localTodo = checkLocalTodoExists.get();

                localTodo.setTitle(todo.getTitle());
                localTodo.setDescription(todo.getDescription());
                localTodo.setTypeId(todo.getTypeId());

                return todoRepository.save(localTodo);
            }
        }

        Optional<Todo> checkLocalTodoExists = todoRepository.findById(todoId);

        if (checkLocalTodoExists.isEmpty()) {
            throw new TodoNotFoundException();
        }

        Todo localTodo = checkLocalTodoExists.get();

        localTodo.setTitle(todo.getTitle());
        localTodo.setDescription(todo.getDescription());
        localTodo.setTypeId(todo.getTypeId());

        return todoRepository.save(localTodo);
    }

    public void delete(UserAuthenticatedDTO userAuthenticated ,UUID todoId) {
        if (userAuthenticated.getTypeId() >= 2) {
            try {
                Todo todo = remoteTodoRepository.getById(todoId);

                UUID remoteTodoId = todo.getId();

                try {
                    remoteTodoRepository.delete(remoteTodoId);
                    return;
                } catch (HttpClientErrorException error) {
                    throw new FailedToDeleteRemoteTodoException();
                }
            } catch (HttpClientErrorException error) {
                boolean checkTodoExists = todoRepository.existsById(todoId);

                if (!checkTodoExists) {
                    throw new TodoNotFoundException();
                }

                todoRepository.deleteById(todoId);
            }

        }
        boolean checkTodoExists = todoRepository.existsById(todoId);

        if (!checkTodoExists) {
            throw new TodoNotFoundException();
        }

        todoRepository.deleteById(todoId);
    }

    public List<Todo> list(UserAuthenticatedDTO userAuthenticated, String title) {
        if (userAuthenticated.getTypeId() >= 2) {
            try {
                GetRemoteTodo[] remoteTodos = remoteTodoRepository.list();

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
                        .filter(todo -> todo.getTitle().toLowerCase(Locale.ROOT).contains(title.toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());

                todosWithTitle.addAll(todoRepository.findByTitleContainingIgnoreCase(title));
                return todosWithTitle;
            } catch (HttpClientErrorException error) {
                throw new FailedToGetRemoteTodoListException();
            }
        }
        if (title != null) {
            return todoRepository.findByTitleContainingIgnoreCase(title);
        } else {
            return todoRepository.findAll();
        }
    }
}
