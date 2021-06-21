package com.todo.spring.modules.todos.repositories;

import com.todo.spring.modules.todos.dtos.CreateRemoteTodoDTO;
import com.todo.spring.modules.todos.dtos.GetRemoteTodo;
import com.todo.spring.modules.todos.dtos.UpdateRemoteTodoDTO;
import com.todo.spring.modules.todos.dtos.UpdateTodoDTO;
import com.todo.spring.modules.todos.models.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Repository
public class RemoteTodoRepository {
    @Autowired
    @Qualifier("RemoteTodoClient")
    private RestTemplate restTemplate;

    public Todo create(CreateRemoteTodoDTO remoteTodo) {
        HttpEntity<CreateRemoteTodoDTO> entity = new HttpEntity<CreateRemoteTodoDTO>(remoteTodo);

        ResponseEntity<Todo> response = restTemplate.postForEntity(
                "/todo",
                entity,
                Todo.class
        );

        return response.getBody();
    }

    public Todo getById(UUID todoId) {
        ResponseEntity<Todo> response = this.restTemplate.getForEntity(
                "/todo/" + todoId,
                Todo.class
        );

        return response.getBody();
    }

    public UpdateRemoteTodoDTO getByIdForUpdate(UUID todoId) {
        ResponseEntity<UpdateRemoteTodoDTO> response = this.restTemplate.getForEntity(
                "/todo/" + todoId,
                UpdateRemoteTodoDTO.class
        );

        return response.getBody();
    }

    public void update(UpdateRemoteTodoDTO todoForUpdate) {
        HttpEntity<UpdateRemoteTodoDTO> putEntity = new HttpEntity<>(todoForUpdate);

        restTemplate.put(
                "/todo",
                putEntity
        );
    }

    public void delete(UUID remoteTodoId) {
        restTemplate.delete(
                "/todo/" + remoteTodoId
        );
    }

    public GetRemoteTodo[] list() {
        ResponseEntity<GetRemoteTodo[]> response = restTemplate.getForEntity(
                "/todo",
                GetRemoteTodo[].class
        );

        return response.getBody();
    }
}
