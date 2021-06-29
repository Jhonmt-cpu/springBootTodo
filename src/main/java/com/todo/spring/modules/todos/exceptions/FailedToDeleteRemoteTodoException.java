package com.todo.spring.modules.todos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FailedToDeleteRemoteTodoException extends ResponseStatusException {
    public FailedToDeleteRemoteTodoException() {
        super(HttpStatus.BAD_REQUEST, "Failed to delete the todo, try again later");
    }
}
