package com.todo.spring.modules.todos.services;

import com.todo.spring.modules.todos.dtos.CreateTodoDTO;
import com.todo.spring.modules.todos.dtos.GetRemoteTodo;
import com.todo.spring.modules.todos.dtos.UpdateRemoteTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateTodoDTO;
import com.todo.spring.modules.todos.exceptions.*;
import com.todo.spring.modules.todos.models.Todo;
import com.todo.spring.modules.todos.repositories.RemoteTodoRepository;
import com.todo.spring.modules.todos.repositories.TodoRepository;
import com.todo.spring.modules.todos.repositories.TodoTypesRepository;
import com.todo.spring.modules.users.dtos.UserAuthenticatedDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private RemoteTodoRepository remoteTodoRepository;

    @Mock
    private TodoTypesRepository todoTypesRepository;

    @InjectMocks
    private TodoService service;

    private UserAuthenticatedDTO userDefaultAuthenticated;

    private UserAuthenticatedDTO userPremiumAuthenticated;

    private Todo localTodoTest;

    private Todo remoteTodoTest;

    private Todo updatedLocalTodoTest;

    private Todo updatedRemoteTodoTest;

    private List<Todo> localTodos;

    private List<Todo> localTodosByTitle;

    private List<Todo> remoteTodosParsed;

    @BeforeEach
    private void openMocks() {
        MockitoAnnotations.openMocks(this);
        userDefaultAuthenticated = UserAuthenticatedDTO.builder()
                .id(UUID.randomUUID())
                .email("johndoe@test.com")
                .typeId(1L)
                .build();

        userPremiumAuthenticated = UserAuthenticatedDTO.builder()
                .id(UUID.randomUUID())
                .email("johndoepremium@test.com")
                .typeId(2L)
                .build();

        localTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Default Local Test")
                .description("Local Todo for tests")
                .typeId(1L)
                .userId(userDefaultAuthenticated.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        remoteTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Default Remote Todo Test")
                .description("Remote Todo for tests")
                .typeId(1L)
                .userId(userPremiumAuthenticated.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updatedLocalTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Updated Local Test")
                .description("Local Todo updated for tests")
                .typeId(1L)
                .userId(userDefaultAuthenticated.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updatedRemoteTodoTest = Todo.builder()
                .id(UUID.randomUUID())
                .title("Updated Remote Test")
                .description("Remote Todo updated for tests")
                .typeId(1L)
                .userId(userPremiumAuthenticated.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Todo localTodo = Todo.builder()
                .id(UUID.randomUUID())
                .title("Local todo 1 test")
                .description("Local Todo for tests")
                .typeId(1L)
                .userId(userDefaultAuthenticated.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Todo anotherLocalTodo = Todo.builder()
                .id(UUID.randomUUID())
                .title("Local todo 1 test")
                .description("Todo 2 test")
                .typeId(1L)
                .userId(userDefaultAuthenticated.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        localTodos = new ArrayList<>();
        localTodos.add(localTodoTest);
        localTodos.add(localTodo);
        localTodos.add(anotherLocalTodo);

        localTodosByTitle = new ArrayList<>();
        localTodosByTitle.add(localTodoTest);
        localTodosByTitle.add(localTodo);


        Todo remoteTodo1 = Todo.builder()
                .id(remoteTodoTest.getId())
                .title("Remote todo 1 test")
                .description("Remote Todo for tests")
                .typeId(1L)
                .userId(userPremiumAuthenticated.getId())
                .createdAt(remoteTodoTest.getCreatedAt())
                .updatedAt(remoteTodoTest.getUpdatedAt())
                .build();

        Todo remoteTodo2 = Todo.builder()
                .id(remoteTodoTest.getId())
                .title("Todo 2 test")
                .description("Remote Todo for tests")
                .typeId(1L)
                .userId(userPremiumAuthenticated.getId())
                .createdAt(remoteTodoTest.getCreatedAt())
                .updatedAt(remoteTodoTest.getUpdatedAt())
                .build();

        remoteTodosParsed = new ArrayList<>();
        remoteTodosParsed.add(remoteTodoTest);
        remoteTodosParsed.add(remoteTodo1);
        remoteTodosParsed.add(remoteTodo2);
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

    private void booleanLocalTodoExistsById() {
        when(todoRepository.existsById(any()))
                .thenReturn(true);
    }

    private void localTodoDoesNotExistsById() {
        when(todoRepository.findById(any()))
                .thenReturn(Optional.empty());
    }

    private void booleanLocalTodoDoesNotExistsById() {
        when(todoRepository.existsById(any()))
                .thenReturn(false);
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

    private void deleteRemoteTodoError() {
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
                .when(remoteTodoRepository)
                .delete(any());
    }

    private void returnAllLocalTodos() {
        when(todoRepository.findAll())
                .thenReturn(localTodos);
    }

    private void returnLocalTodosByTitle() {
        when(todoRepository.findByTitleContainingIgnoreCase(anyString()))
                .thenReturn(localTodosByTitle);
    }

    private void returnEmptyLocalTodos() {
        when(todoRepository.findByTitleContainingIgnoreCase(anyString()))
                .thenReturn(new ArrayList<>());
    }

    private void returnAllRemoteTodos() {
        GetRemoteTodo getRemoteTodo = GetRemoteTodo.builder()
                .id(remoteTodoTest.getId().toString())
                .title(remoteTodoTest.getTitle())
                .description(remoteTodoTest.getDescription())
                .localOwner("Jão")
                .userId(remoteTodoTest.getUserId().toString())
                .typeId(remoteTodoTest.getTypeId())
                .createdAt(remoteTodoTest.getCreatedAt())
                .updatedAt(remoteTodoTest.getUpdatedAt())
                .build();

        GetRemoteTodo getRemoteTodo1 = GetRemoteTodo.builder()
                .id(remoteTodoTest.getId().toString())
                .title("Remote todo 1 test")
                .description(remoteTodoTest.getDescription())
                .localOwner("Jão")
                .userId(remoteTodoTest.getUserId().toString())
                .typeId(remoteTodoTest.getTypeId())
                .createdAt(remoteTodoTest.getCreatedAt())
                .updatedAt(remoteTodoTest.getUpdatedAt())
                .build();

        GetRemoteTodo getRemoteTodo2 = GetRemoteTodo.builder()
                .id(remoteTodoTest.getId().toString())
                .title("Todo 2 test")
                .description(remoteTodoTest.getDescription())
                .localOwner("Jão")
                .userId(remoteTodoTest.getUserId().toString())
                .typeId(remoteTodoTest.getTypeId())
                .createdAt(remoteTodoTest.getCreatedAt())
                .updatedAt(remoteTodoTest.getUpdatedAt())
                .build();

        GetRemoteTodo[] getRemoteTodos = {getRemoteTodo, getRemoteTodo1, getRemoteTodo2};
        when(remoteTodoRepository.list())
                .thenReturn(getRemoteTodos);
    }

    private void listRemoteTodosError() {
        when(remoteTodoRepository.list())
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
    }

    @Test
    void createLocalTodo() {
        todoTypeExistsById();
        saveLocalTodo();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(localTodoTest.getTitle())
                .description(localTodoTest.getDescription())
                .typeId(localTodoTest.getTypeId())
                .build();

        Todo todo = service.create(userDefaultAuthenticated, newTodo);

        assertEquals(localTodoTest.getTitle(), todo.getTitle());
        assertEquals(localTodoTest.getDescription(), todo.getDescription());
        assertEquals(localTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(localTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void createRemoteTodo() {
        todoTypeExistsById();
        saveRemoteTodo();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(remoteTodoTest.getTitle())
                .description(remoteTodoTest.getDescription())
                .typeId(remoteTodoTest.getTypeId())
                .build();

        Todo todo = service.create(userPremiumAuthenticated ,newTodo);

        assertEquals(remoteTodoTest.getTitle(), todo.getTitle());
        assertEquals(remoteTodoTest.getDescription(), todo.getDescription());
        assertEquals(remoteTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(remoteTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldNotCreateAnyTodoIfTodoTypeDoesNotExists() {
        todoTypeDoesNotExistsById();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(localTodoTest.getTitle())
                .description(localTodoTest.getDescription())
                .typeId(6165654L)
                .build();

        assertThrows(
                TodoTypeNotFoundException.class,
                () -> service.create(userDefaultAuthenticated, newTodo)
        );
    }

    @Test
    void shouldNotCreateRemoteTodoIfRegisterRemoteTodoFails() {
        todoTypeExistsById();
        saveRemoteTodoError();

        CreateTodoDTO newTodo = CreateTodoDTO.builder()
                .title(remoteTodoTest.getTitle())
                .description(remoteTodoTest.getDescription())
                .typeId(remoteTodoTest.getTypeId())
                .build();

        assertThrows(
                FailedToRegisterRemoteTodoException.class,
                () -> service.create(userPremiumAuthenticated, newTodo)
        );
    }

    @Test
    void showLocalTodo() {
        localTodoExistsById();

        Todo todo = service.show(userDefaultAuthenticated, localTodoTest.getId());

        assertEquals(localTodoTest.getTitle(), todo.getTitle());
        assertEquals(localTodoTest.getDescription(), todo.getDescription());
        assertEquals(localTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(localTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void showRemoteTodo() {
        remoteTodoExistsById();

        Todo todo = service.show(userPremiumAuthenticated, remoteTodoTest.getId());

        assertEquals(remoteTodoTest.getTitle(), todo.getTitle());
        assertEquals(remoteTodoTest.getDescription(), todo.getDescription());
        assertEquals(remoteTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(remoteTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldShowRemoteTodoIfItDoesNotExistsInTheRemoteAPIButItDoesExistsInLocal() {
        remoteTodoDoesNotExistsById();
        localTodoExistsById();

        Todo todo = service.show(userPremiumAuthenticated, localTodoTest.getId());

        assertEquals(localTodoTest.getTitle(), todo.getTitle());
        assertEquals(localTodoTest.getDescription(), todo.getDescription());
        assertEquals(localTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(localTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldNotShowLocalTodoIfItDoesNotExists () {
        localTodoDoesNotExistsById();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.show(userDefaultAuthenticated, localTodoTest.getId())
        );
    }

    @Test
    void shouldNotShowAnyTodoIfItDoesNotExistsInTheRemoteApiAndLocal () {
        remoteTodoDoesNotExistsById();
        localTodoDoesNotExistsById();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.show(userPremiumAuthenticated, localTodoTest.getId())
        );
    }

    @Test
    void updateLocalTodo() {
        todoTypeExistsById();
        localTodoExistsById();
        updateLocalTodoDB();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(updatedLocalTodoTest.getTypeId())
                .build();

        Todo todo = service.update(userDefaultAuthenticated, updatedLocalTodoTest.getId(), updateTodo);

        assertEquals(updatedLocalTodoTest.getTitle(), todo.getTitle());
        assertEquals(updatedLocalTodoTest.getDescription(), todo.getDescription());
        assertEquals(updatedLocalTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(updatedLocalTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void updateRemoteTodo() {
        todoTypeExistsById();
        remoteTodoExistsForUpdateById();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedRemoteTodoTest.getTitle())
                .description(updatedRemoteTodoTest.getDescription())
                .typeId(updatedRemoteTodoTest.getTypeId())
                .build();

        Todo todo = service.update(userPremiumAuthenticated, updatedRemoteTodoTest.getId(), updateTodo);

        assertEquals(updatedRemoteTodoTest.getTitle(), todo.getTitle());
        assertEquals(updatedRemoteTodoTest.getDescription(), todo.getDescription());
        assertEquals(updatedRemoteTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(updatedRemoteTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldUpdateTodoIfITDoesNotExistsInTheRemoteApiButExistsInLocal() {
        todoTypeExistsById();
        remoteTodoDoesNotExistsForUpdateById();
        localTodoExistsById();
        updateLocalTodoDB();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(updatedLocalTodoTest.getTypeId())
                .build();

        Todo todo = service.update(userPremiumAuthenticated, updatedLocalTodoTest.getId(), updateTodo);

        assertEquals(updatedLocalTodoTest.getTitle(), todo.getTitle());
        assertEquals(updatedLocalTodoTest.getDescription(), todo.getDescription());
        assertEquals(updatedLocalTodoTest.getTypeId(), todo.getTypeId());
        assertEquals(updatedLocalTodoTest.getUserId(), todo.getUserId());
    }

    @Test
    void shouldNotUpdateAnyTodoIfTodoTypeDoesNotExists() {
        todoTypeDoesNotExistsById();

        UpdateTodoDTO updateTodo = UpdateTodoDTO.builder()
                .title(updatedLocalTodoTest.getTitle())
                .description(updatedLocalTodoTest.getDescription())
                .typeId(1651651L)
                .build();

        assertThrows(
                TodoTypeNotFoundException.class,
                () -> service.update(
                        userDefaultAuthenticated,
                        updatedLocalTodoTest.getId(),
                        updateTodo
                ));
    }

    @Test
    void shouldNotUpdateLocalTodoIfItDoesNotExists() {
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
                        userDefaultAuthenticated,
                        UUID.randomUUID(),
                        updateTodo
                ));
    }

    @Test
    void shouldNotUpdateRemoteTodoIfUpdateRemoteTodoFails() {
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
                        userPremiumAuthenticated,
                        updatedRemoteTodoTest.getId(),
                        updateTodo
                ));
    }

    @Test
    void shouldNotUpdateTodoIfItDoesNotExistsRemotelyAndLocally () {
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
                        userPremiumAuthenticated,
                        updatedRemoteTodoTest.getId(),
                        updateTodo
                ));
    }

    @Test
    void deleteLocalTodo() {
        booleanLocalTodoExistsById();

        assertDoesNotThrow(() -> service.delete(userDefaultAuthenticated, localTodoTest.getId()));
    }

    @Test
    void deleteRemoteTodo() {
        remoteTodoExistsById();

        assertDoesNotThrow(() -> service.delete(userPremiumAuthenticated, localTodoTest.getId()));
    }

    @Test
    void shouldDeleteTodoIfItDoesNotExistsInTheRemoteApiButExistsInLocal() {
        remoteTodoDoesNotExistsById();
        booleanLocalTodoExistsById();

        assertDoesNotThrow(() -> service.delete(userPremiumAuthenticated, localTodoTest.getId()));
    }

    @Test
    void shouldNotDeleteLocalTodoIfItDoesNotExists() {
        booleanLocalTodoDoesNotExistsById();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.delete(userDefaultAuthenticated, UUID.randomUUID())
        );
    }

    @Test
    void shouldNotDeleteTodoIfItDoesNotExistsRemotelyOrLocally() {
        remoteTodoDoesNotExistsById();
        booleanLocalTodoDoesNotExistsById();

        assertThrows(
                TodoNotFoundException.class,
                () -> service.delete(userPremiumAuthenticated, UUID.randomUUID())
        );
    }

    @Test
    void shouldNotDeleteRemoteTodoIfDeleteRemoteTodoFails() {
        remoteTodoExistsById();
        deleteRemoteTodoError();

        assertThrows(
                FailedToDeleteRemoteTodoException.class,
                () -> service.delete(userPremiumAuthenticated, localTodoTest.getId())
        );
    }

    @Test
    void listAllLocalTodos() {
        returnAllLocalTodos();

        List<Todo> todos = service.list(userDefaultAuthenticated, null);

        assertEquals(localTodos, todos);
    }

    @Test
    void listLocalTodosByTitle() {
        returnLocalTodosByTitle();

        List<Todo> todos = service.list(userDefaultAuthenticated, "local");

        assertEquals(localTodosByTitle, todos);
    }

    @Test
    void listRemoteTodos() {
        returnAllRemoteTodos();
        returnAllLocalTodos();

        List<Todo> todos = service.list(userPremiumAuthenticated, null);

        List<Todo> expectTodoListResponse = remoteTodosParsed;
        expectTodoListResponse.addAll(localTodos);

        assertEquals(expectTodoListResponse, todos);
    }

    @Test
    void listRemoteTodosByTitle() {
        returnAllRemoteTodos();
        returnEmptyLocalTodos();

        List<Todo> todos = service.list(userPremiumAuthenticated, "remote");

        Todo remoteTodo1 = Todo.builder()
                .id(remoteTodoTest.getId())
                .title("Remote todo 1 test")
                .description("Remote Todo for tests")
                .typeId(1L)
                .userId(userPremiumAuthenticated.getId())
                .createdAt(remoteTodoTest.getCreatedAt())
                .updatedAt(remoteTodoTest.getUpdatedAt())
                .build();

        List<Todo> expectTodoListResponse = new ArrayList<>();
        expectTodoListResponse.add(remoteTodoTest);
        expectTodoListResponse.add(remoteTodo1);

        assertEquals(expectTodoListResponse, todos);
    }

    @Test
    void shouldNotListRemoteTodosIfListRemoteFails() {
        listRemoteTodosError();

        assertThrows(
                FailedToGetRemoteTodoListException.class,
                () -> service.list(userPremiumAuthenticated, null)
        );
    }
}