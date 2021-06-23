package com.todo.spring.modules.todos.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FailedToRegisterRemoteTodoException extends ResponseStatusException {
    public FailedToRegisterRemoteTodoException() {
        super(HttpStatus.BAD_REQUEST, "Failed to register todo");
    }
}
