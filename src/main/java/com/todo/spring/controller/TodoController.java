package com.todo.spring.controller;

import com.todo.spring.models.Todo;
import com.todo.spring.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo create(@RequestBody Todo todo) {
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        return  todoRepository.save(todo);
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
