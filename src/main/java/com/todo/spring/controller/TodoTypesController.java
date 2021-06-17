package com.todo.spring.controller;

import com.todo.spring.models.TodoType;
import com.todo.spring.services.CreateTodoTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("todo-types")
public class TodoTypesController {
    @Resource(name = "CreateTodoTypeService")
    CreateTodoTypeService createTodoTypeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public TodoType create(@RequestBody TodoType todoType) {
        return createTodoTypeService.execute(todoType.getName());
    }
}
