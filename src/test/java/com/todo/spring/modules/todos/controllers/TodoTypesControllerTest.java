package com.todo.spring.modules.todos.controllers;

import com.todo.spring.Application;
import com.todo.spring.modules.todos.models.TodoType;
import com.todo.spring.modules.todos.services.TodoTypeService;
import com.todo.spring.modules.users.models.User;
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
class TodoTypesControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoTypeService todoTypeService;

    TodoType responseBean = TodoType.builder()
            .id(4L)
            .name("Todo Type Test")
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
    void shouldCreateTodoTypeWithAdminUser() throws Exception {
        when(todoTypeService.create(anyString()))
                .thenReturn(responseBean);

        mvc.perform(MockMvcRequestBuilders.post("/user-types")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminUserToken)
                .content("{\"name\": \"Todo Type Test\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(
                                Matchers.containsString("\"name\":\"Todo Type Test\"")
                        )
                );
    }

    @Test
    void shouldNotCreateTodoTypeWithoutToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Todo Type Test\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void shouldNotCreateTodoTypeWithNonAdminUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user-types")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + nonAdminUserToken)
                .content("{\"name\": \"Todo Type Test\"}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}