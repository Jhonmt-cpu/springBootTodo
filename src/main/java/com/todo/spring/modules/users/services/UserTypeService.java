package com.todo.spring.modules.users.services;

import com.todo.spring.modules.users.models.UserType;
import com.todo.spring.modules.users.repository.UserTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service()
public class UserTypeService {

    @Autowired
    private UserTypesRepository userTypesRepository;

    public UserType create(String name) {
        UserType checkUserTypeAlreadyExists = userTypesRepository.findOneByName(name);

        if (checkUserTypeAlreadyExists == null) {
            UserType userType = UserType.builder().name(name).build();
            return userTypesRepository.save(userType);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User type has already been created"
            );
        }
    }
}
