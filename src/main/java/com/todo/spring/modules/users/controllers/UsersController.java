package com.todo.spring.modules.users.controllers;

import com.todo.spring.modules.users.dtos.AuthenticateUserDTO;
import com.todo.spring.modules.users.dtos.CreateUserDTO;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import com.todo.spring.modules.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UsersController {
    @Autowired
    UsersRepository users;

    @Resource()
    private UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return userService.create(createUserDTO);
    }

    @PatchMapping("/{id}")
    public User turnPremium(@PathVariable UUID id, @RequestHeader String Authorization) {
        System.out.println(Authorization);
        return userService.turnPremium(id);
    }

    @PostMapping("/sessions")
    public ResponseEntity createSession(@RequestBody AuthenticateUserDTO data) {
        String token = userService.createSession(data);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("x-auth-token", token);
        return ResponseEntity.ok().headers(httpHeaders).build();
    }
}
