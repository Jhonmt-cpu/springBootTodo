package com.todo.spring.modules.todos.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRemoteTodo {
    private String id;

    private String title;

    private String description;

    private String localOwner;

    private String userId;

    private Long typeId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
