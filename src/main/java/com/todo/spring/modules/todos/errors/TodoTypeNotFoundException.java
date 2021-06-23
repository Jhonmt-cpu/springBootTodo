package com.todo.spring.modules.todos.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TodoTypeNotFoundException extends ResponseStatusException {
    public TodoTypeNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Todo Type Not Found!");
    }
}
