package com.todo.spring.modules.todos.controllers;

import com.todo.spring.modules.todos.models.TodoType;
import com.todo.spring.modules.todos.services.TodoTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
