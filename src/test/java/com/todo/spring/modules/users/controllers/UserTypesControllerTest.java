package com.todo.spring.modules.users.controllers;

import com.todo.spring.Application;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.models.UserType;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Application.class
)
@AutoConfigureMockMvc
class UserTypesControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserTypeService userTypeService;

    UserType responseBean = UserType.builder()
            .id(4L)
            .name("User Type Test")
            .createdAt(LocalDateTime.now())
            .build();

    private final String nonAdminUserToken = AuthenticationService.createToken(User.builder()
            .id(UUID.randomUUID())
            .name("John Doe")
            .email("johndoe@test.com")
            .password("123456")
            .typeId(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());

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
    void shouldCreateUserTypeWithAdminUser() throws Exception {
        when(userTypeService.create(anyString()))
                .thenReturn(responseBean);

        mvc.perform(MockMvcRequestBuilders.post("/user-types")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminUserToken)
                .content("{\"name\": \"User Type Test\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(
                                Matchers.containsString("\"name\":\"User Type Test\"")
                        )
                );
    }

    @Test
    void shouldNotCreateUserTypeWithoutToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"User Type Test\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldNotCreateUserTypeWithNonAdminUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user-types")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + nonAdminUserToken)
                .content("{\"name\": \"User Type Test\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}