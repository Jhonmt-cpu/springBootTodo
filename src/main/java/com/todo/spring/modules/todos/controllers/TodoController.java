package com.todo.spring.modules.todos.controllers;

import com.todo.spring.modules.todos.dtos.CreateTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateTodoDTO;
import com.todo.spring.modules.todos.models.Todo;
import com.todo.spring.modules.todos.services.TodoService;
import com.todo.spring.modules.users.dtos.UserAuthenticatedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
        UserAuthenticatedDTO userAuthenticated = (UserAuthenticatedDTO) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return todoService.create(userAuthenticated, createTodoDTO);
    }

    @GetMapping("/{todoId}")
    public Todo show(@PathVariable UUID todoId) {
        UserAuthenticatedDTO userAuthenticated = (UserAuthenticatedDTO) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return todoService.show(userAuthenticated, todoId);
    }

    @PutMapping("/{todoId}")
    public Todo update(
            @PathVariable UUID todoId,
            @Valid  @RequestBody UpdateTodoDTO todo) {
        UserAuthenticatedDTO userAuthenticated = (UserAuthenticatedDTO) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return todoService.update(userAuthenticated, todoId, todo);
    }

    @DeleteMapping("/{todoId}")
    public void delete(@PathVariable UUID todoId) {
        UserAuthenticatedDTO userAuthenticated = (UserAuthenticatedDTO) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        todoService.delete(userAuthenticated, todoId);
    }

    @GetMapping()
    public List<Todo> list(@RequestParam(required = false) String title) {
        UserAuthenticatedDTO userAuthenticated = (UserAuthenticatedDTO) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return todoService.list(userAuthenticated, title);
    }
}
