package com.todo.spring.modules.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidLoginException extends ResponseStatusException {
    public InvalidLoginException() {
        super(HttpStatus.BAD_REQUEST, "Email or password is incorrect");
    }
}
