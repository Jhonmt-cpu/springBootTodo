package com.todo.spring.modules.todos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTodoDTO {
    @NotEmpty(message = "Todo title cannot be empty")
    private String title;

    @NotEmpty(message = "Todo description cannot be empty")
    private String description;

    @NotNull(message = "Type id is required")
    private Long typeId;
}
