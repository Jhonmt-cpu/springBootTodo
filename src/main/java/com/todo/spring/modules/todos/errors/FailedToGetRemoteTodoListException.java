package com.todo.spring.modules.todos.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FailedToGetRemoteTodoListException extends ResponseStatusException {
    public FailedToGetRemoteTodoListException() {
        super(HttpStatus.NOT_FOUND, "Failed to get the todo list, try again later");
    }
}
