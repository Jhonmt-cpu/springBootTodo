package com.todo.spring.modules.todos.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FailedToUpdateRemoteTodoException extends ResponseStatusException {
    public FailedToUpdateRemoteTodoException() {
        super(HttpStatus.BAD_REQUEST, "Failed to update the todo, try again later");
    }
}
