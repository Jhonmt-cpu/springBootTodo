package com.todo.spring.modules.todos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRemoteTodoDTO {
    private UUID id;

    private String title;

    private String description;

    private UUID userId;

    private Long typeId;

    private String updatedAt;

    private String createdAt;
}
