package com.todo.spring.modules.users.services;

import com.todo.spring.modules.users.dtos.CreateUserDTO;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService service;

    private User userTest;

    @BeforeEach
    private void openMocks() {
        MockitoAnnotations.openMocks(this);
        userTest = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("johndoe@test.com")
                .password("123456")
                .typeId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void userDoesNotExistsWithSameEmail() {
        when(usersRepository.findOneByEmail(anyString()))
                .thenReturn(null);
    }

    public void userAlreadyExistsWithSameEmail() {
        when(usersRepository.findOneByEmail(anyString()))
                .thenReturn(userTest);
    }

    public void userExistsWithId() {
        when(usersRepository.findById(any()))
                .thenReturn(Optional.of(userTest));
    }

    public void userDoesNotExistsWithId() {
        when(usersRepository.findById(any()))
                .thenReturn(Optional.empty());
    }

    public void saveUserInDB() {
        when(usersRepository.save(any()))
                .thenReturn(userTest);
    }

    public void savePremiumUserInDB() {
        when(usersRepository.save(any()))
                .thenReturn(User.builder()
                        .id(userTest.getId())
                        .name(userTest.getName())
                        .email(userTest.getEmail())
                        .password(userTest.getPassword())
                        .typeId(2L)
                        .createdAt(userTest.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build()
                );
    }

    @Test
    void shouldCreateUser() {
        userDoesNotExistsWithSameEmail();
        saveUserInDB();

        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("John Doe")
                .email("johndoe@test.com")
                .password("123456")
                .build();

        User user = service.create(createUserDTO);

        assertEquals("John Doe", user.getName());
        assertEquals("johndoe@test.com", user.getEmail());
        assertEquals(1, user.getTypeId());
    }

    @Test
    void shouldNotCreateUserWithExistingEmail() {
        userAlreadyExistsWithSameEmail();

        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .name("John Doe")
                .email("johndoe@test.com")
                .password("123456")
                .build();

        assertThrows(ResponseStatusException.class, () -> service.create(createUserDTO));
    }

    @Test
    void turnExistingUserPremium() {
        userExistsWithId();
        savePremiumUserInDB();

        User user = service.turnPremium(userTest.getId());

        assertEquals(2, user.getTypeId());
    }

    @Test
    void shouldNotTurnANonExistingUserToPremium() {
        userDoesNotExistsWithId();

        assertThrows(ResponseStatusException.class, () -> service.turnPremium(userTest.getId()));
    }
}