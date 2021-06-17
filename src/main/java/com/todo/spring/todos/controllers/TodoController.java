package com.todo.spring.todos.controllers;

import com.todo.spring.todos.dtos.CreateTodoDTO;
import com.todo.spring.todos.models.Todo;
import com.todo.spring.todos.repositories.TodoRepository;
import com.todo.spring.todos.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Resource
    private TodoService todoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@Valid @RequestBody CreateTodoDTO createTodoDTO) {
        return todoService.create(createTodoDTO);
    }

    @GetMapping("/{id}")
    public Todo find(@PathVariable UUID id) {
        return todoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Todo not found"
        ));
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable UUID id, @RequestBody Todo todo) {
        Todo todoFromDB = todoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Todo not found"
        ));
        todoFromDB.setTitle(todo.getTitle());
        todoFromDB.setUpdatedAt(LocalDateTime.now());
        todoRepository.save(todoFromDB);
        return todoFromDB;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        todoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Todo not found"
        ));
        todoRepository.deleteById(id);
    }

    @GetMapping()
    public List<Todo> list(@RequestParam(required = false) String title) {
        if (title != null) {
            return todoRepository.findByTitleContainingIgnoreCase(title);
        } else {
            return todoRepository.findAll();
        }
    }
}
