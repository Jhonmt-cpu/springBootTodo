package com.todo.spring.modules.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticateUserDTO {
    @NotEmpty(message = "Username is required")
    @Email
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;
}
