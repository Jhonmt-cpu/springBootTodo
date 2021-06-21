package com.todo.spring.modules.todos.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRemoteTodoDTO implements Serializable {
    private String title;

    private String description;

    private String localOwner;

    private UUID userId;

    private Long typeId;

    private String updatedAt;
}
