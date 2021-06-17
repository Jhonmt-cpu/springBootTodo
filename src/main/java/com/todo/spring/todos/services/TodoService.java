package com.todo.spring.todos.services;

import com.todo.spring.todos.dtos.CreateTodoDTO;
import com.todo.spring.todos.models.Todo;
import com.todo.spring.todos.repositories.TodoRepository;
import com.todo.spring.todos.repositories.TodoTypesRepository;
import com.todo.spring.users.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TodoTypesRepository todoTypesRepository;

    public Todo create(CreateTodoDTO createTodoDTO) {
        usersRepository.findById(createTodoDTO.getUserId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User not found"
        ));

        boolean checkTodoTypeExists = todoTypesRepository.existsById(createTodoDTO.getTypeId());

        if (!checkTodoTypeExists) {
            throw  new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Todo type doesn't exists"
            );
        }

        Todo todo = Todo.builder()
                .title(createTodoDTO.getTitle())
                .description(createTodoDTO.getDescription())
                .typeId(createTodoDTO.getTypeId())
                .userId(createTodoDTO.getUserId())
                .build();

        return todoRepository.save(todo);
    }
}
