package com.todo.spring.modules.users.controllers;

import com.todo.spring.modules.users.dtos.CreateUserDTO;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UsersController {

    @Resource()
    private UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody CreateUserDTO createUserDTO) {
        return userService.create(createUserDTO);
    }

    @PatchMapping("/{id}")
    public User turnPremium(@PathVariable UUID id) {
        return userService.turnPremium(id);
    }
}
