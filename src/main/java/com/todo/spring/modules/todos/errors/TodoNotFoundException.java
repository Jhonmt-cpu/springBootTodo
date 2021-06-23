package com.todo.spring.modules.todos.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TodoNotFoundException extends ResponseStatusException {
    public TodoNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Todo doesn't exists");
    }
}
