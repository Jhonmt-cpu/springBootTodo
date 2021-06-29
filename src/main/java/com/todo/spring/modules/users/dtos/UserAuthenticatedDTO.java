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

    private String name;

    private String email;

    private String token;

    private Long typeId;

    public static UserAuthenticatedDTO toDTO(User user, String token) {
        return UserAuthenticatedDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .typeId(user.getTypeId())
                .build();
    }

}
