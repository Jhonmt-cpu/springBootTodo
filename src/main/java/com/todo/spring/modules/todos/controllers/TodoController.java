package com.todo.spring.modules.todos.controllers;

import com.todo.spring.modules.todos.dtos.CreateTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateTodoDTO;
import com.todo.spring.modules.todos.models.Todo;
import com.todo.spring.modules.todos.services.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Resource
    private TodoService todoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@Valid @RequestBody CreateTodoDTO createTodoDTO) {
        return todoService.create(createTodoDTO);
    }

    @GetMapping("/{userId}/{todoId}")
    public Todo show(@PathVariable UUID userId, @PathVariable UUID todoId) {
        return todoService.show(todoId, userId);
    }

    @PutMapping("/{userId}/{todoId}")
    public Todo update(@PathVariable UUID userId, @PathVariable UUID todoId, @Valid  @RequestBody UpdateTodoDTO todo) {
        return todoService.update(userId, todoId, todo);
    }

    @DeleteMapping("/{userId}/{todoId}")
    public void delete(@PathVariable UUID userId, @PathVariable UUID todoId) {
        todoService.delete(userId, todoId);
    }

    @GetMapping("/{userId}")
    public List<Todo> list(@PathVariable UUID userId ,@RequestParam(required = false) String title) {
        return todoService.list(userId, title);
    }
}
