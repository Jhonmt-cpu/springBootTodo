package com.todo.spring.modules.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserDTO {
    @NotEmpty(message = "User name cannot be empty")
    private String name;

    @NotEmpty(message = "User email cannot be empty")
    private String email;

    @NotEmpty(message = "User email cannot be empty")
    private String password;
}
