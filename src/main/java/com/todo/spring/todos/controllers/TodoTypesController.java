package com.todo.spring.todos.controllers;

import com.todo.spring.todos.models.TodoType;
import com.todo.spring.todos.services.TodoTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("todo-types")
public class TodoTypesController {
    @Resource()
    TodoTypeService todoTypeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public TodoType create(@Valid @RequestBody TodoType todoType) {
        return todoTypeService.create(todoType.getName());
    }
}
