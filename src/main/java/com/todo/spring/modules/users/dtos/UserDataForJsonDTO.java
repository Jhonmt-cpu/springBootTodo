package com.todo.spring.modules.users.dtos;

import com.todo.spring.modules.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDataForJsonDTO {
    private UUID id;

    private Long typeId;

    public static UserDataForJsonDTO toDto(User user) {
        return UserDataForJsonDTO.builder()
                .id(user.getId())
                .typeId(user.getTypeId())
                .build();
    }
}
