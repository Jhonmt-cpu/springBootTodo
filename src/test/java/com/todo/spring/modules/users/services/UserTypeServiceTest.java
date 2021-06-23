package com.todo.spring.modules.users.services;

import com.todo.spring.modules.users.models.UserType;
import com.todo.spring.modules.users.repository.UserTypesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTypeServiceTest {

    @Mock
    private UserTypesRepository userTypesRepository;

    @InjectMocks
    private UserTypeService service;

    private UserType userTypeTest;

    @BeforeEach
    private void openMocks() {
        MockitoAnnotations.openMocks(this);
        userTypeTest = UserType.builder()
                .id(1L)
                .name("Default Test")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void userTypeDoesNotExistsByName() {
        when(userTypesRepository.findOneByName(anyString()))
                .thenReturn(null);
    }

    private void userTypesAlreadyExistsByName() {
        when(userTypesRepository.findOneByName(anyString()))
                .thenReturn(userTypeTest);
    }

    private void saveUserTypeInDB() {
        when(userTypesRepository.save(any()))
                .thenReturn(userTypeTest);
    }

    @Test
    void create() {
        userTypeDoesNotExistsByName();
        saveUserTypeInDB();

        UserType newUserType = service.create("Default Test");

        assertEquals("Default Test", newUserType.getName());
    }

    @Test
    void shouldNotCreateAUserTypeWithExistingName() {
        userTypesAlreadyExistsByName();

        assertThrows(ResponseStatusException.class, () -> service.create("Default Test"));
    }
}