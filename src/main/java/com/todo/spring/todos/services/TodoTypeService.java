package com.todo.spring.todos.services;

import com.todo.spring.todos.models.TodoType;
import com.todo.spring.todos.repositories.TodoTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service()
public class TodoTypeService {

    @Autowired
    private TodoTypesRepository todoTypesRepository;

    public TodoType create(String name) {
        TodoType checkTodoTypeAlreadyExists = todoTypesRepository.findOneByName(name);

        if (checkTodoTypeAlreadyExists == null) {
            TodoType todoType = TodoType.builder().name(name).build();
            return todoTypesRepository.save(todoType);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Todo type has already been created"
            );
        }
    }
}
