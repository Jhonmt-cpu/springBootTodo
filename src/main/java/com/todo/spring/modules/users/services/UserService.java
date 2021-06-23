package com.todo.spring.modules.users.services;

import com.todo.spring.modules.users.dtos.CreateUserDTO;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service()
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    public User create(CreateUserDTO createUserDTO) {
        User checkUserAlreadyExists = usersRepository.findOneByEmail(createUserDTO.getEmail());

        if (checkUserAlreadyExists != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User already exists"
            );
        }

        User user = User.builder().name(createUserDTO.getName()).email(createUserDTO.getEmail()).password(createUserDTO.getPassword()).typeId(1L).build();

        return usersRepository.save(user);
    }

    public User turnPremium(UUID id) {
        Optional<User> checkUserExists = usersRepository.findById(id);

        if (checkUserExists.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }

        User user = checkUserExists.get();

        user.setTypeId(2L);

        return usersRepository.save(user);
    }
}
