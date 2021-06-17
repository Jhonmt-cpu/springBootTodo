package com.todo.spring.services;

import com.todo.spring.models.TodoType;
import com.todo.spring.repository.TodoTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;

@Service("CreateTodoTypeService")
public class CreateTodoTypeService {

    @Autowired
    private TodoTypesRepository todoTypesRepository;

    public TodoType execute(String name) {
        TodoType checkTodoTypeAlreadyExists = todoTypesRepository.findOneByName(name);

        if (checkTodoTypeAlreadyExists == null) {
            TodoType todoType = TodoType.builder().name(name).build();
            return todoTypesRepository.save(todoType);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Todo type have already been created"
            );
        }
    }
}
