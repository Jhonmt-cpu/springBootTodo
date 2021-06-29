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
public class UserAuthenticatedDTO {
    private UUID id;

    private String email;

    private Long typeId;

    public static UserAuthenticatedDTO toDTO(User user) {
        return UserAuthenticatedDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .typeId(user.getTypeId())
                .build();
    }

}
