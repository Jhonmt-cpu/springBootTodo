package com.todo.spring.todos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTodoDTO {
    @NotEmpty(message = "Todo title cannot be empty")
    private String title;

    @NotEmpty(message = "Todo description cannot be empty")
    private String description;

    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Type id is required")
    private Long typeId;
}
