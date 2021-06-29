package com.todo.spring.modules.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistsException extends ResponseStatusException {
    public UserAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "User Already Exists!");
    }
}
