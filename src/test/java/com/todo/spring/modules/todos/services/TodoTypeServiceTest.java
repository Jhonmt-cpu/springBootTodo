package com.todo.spring.modules.todos.services;

import com.todo.spring.modules.todos.models.TodoType;
import com.todo.spring.modules.todos.repositories.TodoTypesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TodoTypeServiceTest {

    @Mock
    private TodoTypesRepository todoTypesRepository;

    @InjectMocks
    private TodoTypeService service;

    private TodoType todoTypeTest;

    @BeforeEach
    private void openMocks() {
        MockitoAnnotations.openMocks(this);
        todoTypeTest = TodoType.builder()
                .id(1L)
                .name("Default Test")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void todoTypeDoesNotExistsByName() {
        when(todoTypesRepository.findOneByName(anyString()))
                .thenReturn(null);
    }

    private void todoTypesAlreadyExistsByName() {
        when(todoTypesRepository.findOneByName(anyString()))
                .thenReturn(todoTypeTest);
    }

    private void saveUserTypeInDB() {
        when(todoTypesRepository.save(any()))
                .thenReturn(todoTypeTest);
    }

    @Test
    void create() {
        todoTypeDoesNotExistsByName();
        saveUserTypeInDB();

        TodoType newTodoType = service.create("Default Test");

        assertEquals("Default Test", newTodoType.getName());
    }

    @Test
    void shouldNotCreateATodoTypeWithExistingName() {
        todoTypesAlreadyExistsByName();

        assertThrows(ResponseStatusException.class, () -> service.create("Default Test"));
    }
}