package com.todo.spring.modules.users.controllers;

import com.todo.spring.modules.users.models.UserType;
import com.todo.spring.modules.users.services.UserTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("user-types")
public class UserTypesController {
    @Resource()
    private UserTypeService userTypeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserType create(@Valid @RequestBody UserType userType) {
        return userTypeService.create(userType.getName());
    }
}
