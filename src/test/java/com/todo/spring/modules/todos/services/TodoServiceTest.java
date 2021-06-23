package com.todo.spring.modules.todos.services;

import com.todo.spring.modules.todos.dtos.CreateTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateRemoteTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateTodoDTO;
import com.todo.spring.modules.todos.errors.*;
import com.todo.spring.modules.todos.models.Todo;
import com.todo.spring.modules.todos.repositories.RemoteTodoRepository;
import com.todo.spring.modules.todos.repositories.TodoRepository;
import com.todo.spring.modules.todos.repositories.TodoTypesRepository;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private RemoteTodoRepository remoteTodoRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TodoTypesRepository todoTypesRepository;

    @InjectMocks
    private TodoService service;

    private User userDefaultTest;

    private User userPremiumTest;

    private Todo localTodoTest;

    private Todo remoteTodoTest;

    private Todo updatedLocalTodoTest;

    private Todo updatedRemoteTodoTest;

    @BeforeEach
    private void openMocks() {
        MockitoAnnotations.openMocks(this);
        userDefaultTest = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .email("johndoe@test.com")
                .password("123456")
                .typeId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userPremiumTest = User.builder()
                .id(UUID.randomUUID())
                .name("John Doe Premium")
                .email("johndoepremium@test.com")
                .password("123456")
                .typeId(2L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        localTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Default Local Test")
                .description("Local Todo for tests")
                .typeId(1L)
                .userId(userDefaultTest.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        remoteTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Default Remote Test")
                .description("Remote Todo for tests")
                .typeId(1L)
                .userId(userPremiumTest.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updatedLocalTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Updated Local Test")
                .description("Local Todo updated for tests")
                .typeId(1L)
                .userId(userDefaultTest.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updatedRemoteTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Updated Remote Test")
                .description("Remote Todo updated for tests")
                .typeId(1L)
                .userId(userPremiumTest.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void userDefaultExistsById() {
        when(usersRepository.findById(any()))
                .thenReturn(Optional.of(userDefaultTest));
    }

    private void userPremiumExistsById() {
        when(usersRepository.findById(any()))
                .thenReturn(Optional.of(userPremiumTest));
    }

    private void userDoesNotExistsById() {
        when(usersRepository.findById(any()))
                .thenReturn(Optional.empty());
    }

    private void todoTypeExistsById() {
        when(todoTypesRepository.existsById(anyLong()))
                .thenReturn(true);
    }

    private void todoTypeDoesNotExistsById() {
        when(todoTypesRepository.existsById(anyLong()))
                .thenReturn(false);
    }

    private void saveLocalTodo() {
        when(todoRepository.save(any()))
                .thenReturn(localTodoTest);
    }

    private void saveRemoteTodo() {
        when(remoteTodoRepository.create(any()))
                .thenReturn(remoteTodoTest);
    }

    private void saveRemoteTodoError() {
        when(remoteTodoRepository.create(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
    }

    private void localTodoExistsById() {
        when(todoRepository.findById(any()))
                .thenReturn(Optional.of(localTodoTest));
    }

    private void localTodoDoesNotExistsById() {
        when(todoRepository.findById(any()))
                .thenReturn(Optional.empty());
    }

    private void remoteTodoExistsById() {
        when(remoteTodoRepository.getById(any()))
                .thenReturn(remoteTodoTest);
    }

    private void remoteTodoExistsForUpdateById() {
        when(remoteTodoRepository.getByIdForUpdate(any()))
                .thenReturn(UpdateRemoteTodoDTO.builder()
                        .id(updatedRemoteTodoTest.getId())
                        .title(updatedRemoteTodoTest.getTitle())
                        .description(updatedRemoteTodoTest.getDescription())
                        .localOwner("Jão")
                        .typeId(updatedRemoteTodoTest.getTypeId())
                        .userId(updatedRemoteTodoTest.getUserId())
                        .createdAt(updatedRemoteTodoTest.getCreatedAt().toString())
                        .updatedAt(updatedRemoteTodoTest.getUpdatedAt().toString())
                        .build());
    }

    private void remoteTodoDoesNotExistsById() {
        when(remoteTodoRepository.getById(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    private void remoteTodoDoesNotExistsForUpdateById() {
        when(remoteTodoRepository.getByIdForUpdate(any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    private void updateLocalTodoDB() {
        when(todoRepository.save(any()))
                .thenReturn(updatedLocalTodoTest);
    }

    private void updateRemoteTodoError() {
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .when(remoteTodoRepository)
                .update(any());
    }

    @Test
    void createLocalTodo() {
        userDefaultExistsById();
        todoTypeExistsById();
        saveLocalTodo();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(localTodoTest.getTitle())
                .description(localTodoTest.getDescription())
                .typeId(localTodoTest.getTypeId())
                .userId(localTodoTest.getUserId())
                .build();

        Todo todo = service.create(newTodo);

        assertEquals(localTodoTest.getTitle(), todo.getTitle());
        assertEquals(localTodoTest.getDescription(), todo.getDescription());
        assertEquals(localTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(localTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void createRemoteTodo() {
        userPremiumExistsById();
        todoTypeExistsById();
        saveRemoteTodo();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(remoteTodoTest.getTitle())
                .description(remoteTodoTest.getDescription())
                .typeId(remoteTodoTest.getTypeId())
                .userId(remoteTodoTest.getUserId())
                .build();

        Todo todo = service.create(newTodo);

        assertEquals(remoteTodoTest.getTitle(), todo.getTitle());
        assertEquals(remoteTodoTest.getDescription(), todo.getDescription());
        assertEquals(remoteTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(remoteTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldNotCreateAnyTodoIfUserDoesNotExists() {
        userDoesNotExistsById();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(localTodoTest.getTitle())
                .description(localTodoTest.getDescription())
                .typeId(localTodoTest.getTypeId())
                .userId(UUID.randomUUID())
                .build();

        assertThrows(UserNotFoundException.class, () -> service.create(newTodo));
    }

    @Test
    void shouldNotCreateAnyTodoIfTodoTypeDoesNotExists() {
        userDefaultExistsById();
        todoTypeDoesNotExistsById();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(localTodoTest.getTitle())
                .description(localTodoTest.getDescription())
                .typeId(6165654L)
                .userId(localTodoTest.getUserId())
                .build();

        assertThrows(TodoTypeNotFoundException.class, () -> service.create(newTodo));
    }

    @Test
    void shouldNotCreateRemoteTodoIfRegisterRemoteTodoFails() {
        userPremiumExistsById();
        todoTypeExistsById();
        saveRemoteTodoError();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(remoteTodoTest.getTitle())
                .description(remoteTodoTest.getDescription())
                .typeId(remoteTodoTest.getTypeId())
                .userId(remoteTodoTest.getUserId())
                .build();

        assertThrows(FailedToRegisterRemoteTodoException.class, () -> service.create(newTodo));
    }

    @Test
    void showLocalTodo() {
        userDefaultExistsById();
        localTodoExistsById();

        Todo todo = service.show(localTodoTest.getId(), userDefaultTest.getId());

        assertEquals(localTodoTest.getTitle(), todo.getTitle());
        assertEquals(localTodoTest.getDescription(), todo.getDescription());
        assertEquals(localTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(localTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void showRemoteTodo() {
        userPremiumExistsById();
        remoteTodoExistsById();

        Todo todo = service.show(remoteTodoTest.getId(), userPremiumTest.getId());

        assertEquals(remoteTodoTest.getTitle(), todo.getTitle());
        assertEquals(remoteTodoTest.getDescription(), todo.getDescription());
        assertEquals(remoteTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(remoteTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldShowRemoteTodoIfItDoesNotExistsInTheRemoteAPIButItDoesExistsInLocal() {
        userPremiumExistsById();
        remoteTodoDoesNotExistsById();
        localTodoExistsById();

        Todo todo = service.show(localTodoTest.getId(), userPremiumTest.getId());

        assertEquals(localTodoTest.getTitle(), todo.getTitle());
        assertEquals(localTodoTest.getDescription(), todo.getDescription());
        assertEquals(localTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(localTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldNotShowAnyTodoIfUserDoesNotExists() {
        userDoesNotExistsById();

        assertThrows(
                UserNotFoundException.class,
                () -> service.show(localTodoTest.getId(), userDefaultTest.getId())
        );
    }

    @Test
    void shouldNotShowLocalTodoIfItDoesNotExists () {
        userDefaultExistsById();
        localTodoDoesNotExistsById();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.show(localTodoTest.getId(), userDefaultTest.getId())
        );
    }

    @Test
    void shouldNotShowAnyTodoIfItDoesNotExistsInTheRemoteApiAndLocal () {
        userPremiumExistsById();
        remoteTodoDoesNotExistsById();
        localTodoDoesNotExistsById();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.show(localTodoTest.getId(), userDefaultTest.getId())
        );
    }

    @Test
    void updateLocalTodo() {
        userDefaultExistsById();
        todoTypeExistsById();
        localTodoExistsById();
        updateLocalTodoDB();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(updatedLocalTodoTest.getTypeId())
                .build();

        Todo todo = service.update(userDefaultTest.getId(), updatedLocalTodoTest.getId(), updateTodo);

        assertEquals(updatedLocalTodoTest.getTitle(), todo.getTitle());
        assertEquals(updatedLocalTodoTest.getDescription(), todo.getDescription());
        assertEquals(updatedLocalTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(updatedLocalTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void updateRemoteTodo() {
        userPremiumExistsById();
        todoTypeExistsById();
        remoteTodoExistsForUpdateById();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedRemoteTodoTest.getTitle())
                .description(updatedRemoteTodoTest.getDescription())
                .typeId(updatedRemoteTodoTest.getTypeId())
                .build();

        Todo todo = service.update(userPremiumTest.getId(), updatedRemoteTodoTest.getId(), updateTodo);

        assertEquals(updatedRemoteTodoTest.getTitle(), todo.getTitle());
        assertEquals(updatedRemoteTodoTest.getDescription(), todo.getDescription());
        assertEquals(updatedRemoteTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(updatedRemoteTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldUpdateTodoIfITDoesNotExistsInTheRemoteApiButExistsInLocal() {
        userPremiumExistsById();
        todoTypeExistsById();
        remoteTodoDoesNotExistsForUpdateById();
        localTodoExistsById();
        updateLocalTodoDB();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(updatedLocalTodoTest.getTypeId())
                .build();

        Todo todo = service.update(userPremiumTest.getId(), updatedLocalTodoTest.getId(), updateTodo);

        assertEquals(updatedLocalTodoTest.getTitle(), todo.getTitle());
        assertEquals(updatedLocalTodoTest.getDescription(), todo.getDescription());
        assertEquals(updatedLocalTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(updatedLocalTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldNotUpdateAnyTodoIfUserDoesNotExists() {
        userDoesNotExistsById();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(updatedLocalTodoTest.getTypeId())
                .build();

        assertThrows(
                UserNotFoundException.class,
                () -> service.update(
                        UUID.randomUUID(),
                        updatedLocalTodoTest.getId(),
                        updateTodo
                ));
    }

    @Test
    void shouldNotUpdateAnyTodoIfTodoTypeDoesNotExists() {
        userDefaultExistsById();
        todoTypeDoesNotExistsById();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(1651651L)
                .build();

        assertThrows(
                TodoTypeNotFoundException.class,
                () -> service.update(
                        userDefaultTest.getId(),
                        updatedLocalTodoTest.getId(),
                        updateTodo
                ));
    }

    @Test
    void shouldNotUpdateLocalTodoIfItDoesNotExists() {
        userDefaultExistsById();
        todoTypeExistsById();
        localTodoDoesNotExistsById();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(updatedLocalTodoTest.getTypeId())
                .build();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.update(
                        userDefaultTest.getId(),
                        UUID.randomUUID(),
                        updateTodo
                ));
    }

    @Test
    void shouldNotUpdateRemoteTodoIfUpdateRemoteTodoFails() {
        userPremiumExistsById();
        todoTypeExistsById();
        remoteTodoExistsForUpdateById();
        updateRemoteTodoError();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedRemoteTodoTest.getTitle())
                .description(updatedRemoteTodoTest.getDescription())
                .typeId(updatedRemoteTodoTest.getTypeId())
                .build();

        assertThrows(
                FailedToUpdateRemoteTodoException.class,
                () -> service.update(
                        userPremiumTest.getId(),
                        updatedRemoteTodoTest.getId(),
                        updateTodo
                ));
    }

    @Test
    void shouldNotUpdateTodoIfItDoesNotExistsRemotelyAndLocally () {
        userPremiumExistsById();
        todoTypeExistsById();
        remoteTodoDoesNotExistsForUpdateById();
        localTodoDoesNotExistsById();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(updatedLocalTodoTest.getTypeId())
                .build();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.update(
                        userPremiumTest.getId(),
                        updatedRemoteTodoTest.getId(),
                        updateTodo
                ));
    }

    @Test
    void delete() {
    }

    @Test
    void list() {
    }
}