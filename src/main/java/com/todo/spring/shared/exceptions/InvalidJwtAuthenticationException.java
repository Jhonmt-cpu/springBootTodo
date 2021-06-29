package com.todo.spring.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidJwtAuthenticationException extends ResponseStatusException {
    public InvalidJwtAuthenticationException() {
        super(HttpStatus.UNAUTHORIZED, "Expired or invalid JWT token");
    }
}
