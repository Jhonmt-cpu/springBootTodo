package com.todo.spring.modules.users.controllers;

import com.todo.spring.Application;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.models.UserType;
import com.todo.spring.modules.users.services.UserService;
import com.todo.spring.modules.users.services.UserTypeService;
import com.todo.spring.shared.security.AuthenticationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

     private final User responseUserPremium = User.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .email("johndoe@test.com")
            .password("123456")
            .typeId(2L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final User responseUserDefault = User.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .email("johndoe@test.com")
            .password("123456")
            .typeId(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    private final String nonAdminUserToken = AuthenticationService.createToken(responseUserPremium);

    private final String adminUserToken = AuthenticationService.createToken(User.builder()
            .id(UUID.randomUUID())
            .name("John Doe Admin")
            .email("admin@test.com")
            .password("123456")
            .typeId(3L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());

    @Test
    void shouldCreateANewUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(responseUserDefault);

        mvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\",\"email\": \"johndoe@test.com\",\"password\": \"123456\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(
                                Matchers.containsString("\"typeId\":1")
                        )
                );
    }

    @Test
    void shouldTurnPremiumAnUserIfAnAdminUserRequestIt() throws Exception {
        when(userService.turnPremium(any(UUID.class)))
                .thenReturn(responseUserPremium);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{id}", responseUserPremium.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminUserToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(
                                Matchers.containsString("\"typeId\":2")
                        )
                );
    }

    @Test
    void shouldNotTurnPremiumAnUserIfAnNonAdminUserRequestIt() throws Exception {
        when(userService.turnPremium(any(UUID.class)))
                .thenReturn(responseUserPremium);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{id}", responseUserPremium.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + nonAdminUserToken))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldNotTurnPremiumAnUserIfAnNonAuthenticatedUserRequestIt() throws Exception {
        when(userService.turnPremium(any(UUID.class)))
                .thenReturn(responseUserPremium);

        mvc.perform(MockMvcRequestBuilders.patch("/users/{id}", responseUserPremium.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldGenerateTokenAndReturnItInHeader() throws Exception {
        when(userService.createSession(any()))
                .thenReturn(nonAdminUserToken);

        mvc.perform(MockMvcRequestBuilders.post("/users/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"johndoe@test.com\", \"password\": \"123456\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .header()
                        .string("x-auth-token", nonAdminUserToken)
                );
    }
}