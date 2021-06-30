package com.todo.spring.modules.users.services;

import com.todo.spring.modules.users.dtos.AuthenticateUserDTO;
import com.todo.spring.modules.users.dtos.CreateUserDTO;
import com.todo.spring.modules.users.exceptions.InvalidLoginException;
import com.todo.spring.modules.users.exceptions.UserAlreadyExistsException;
import com.todo.spring.modules.users.exceptions.UserNotFoundException;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import com.todo.spring.shared.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Service()
public class UserService {

    @Autowired
    private UsersRepository usersRepository;


    public User create(@NotNull CreateUserDTO createUserDTO) {
        User checkUserAlreadyExists = usersRepository.findOneByEmail(createUserDTO.getEmail());

        if (checkUserAlreadyExists != null) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder().name(createUserDTO.getName()).email(createUserDTO.getEmail()).password(createUserDTO.getPassword()).typeId(1L).build();

        return usersRepository.save(user);
    }

    public User turnPremium(UUID id) {
        Optional<User> checkUserExists = usersRepository.findById(id);

        if (checkUserExists.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = checkUserExists.get();

        user.setTypeId(2L);

        return usersRepository.save(user);
    }

    public String createSession(AuthenticateUserDTO data) {
        User checkUserExists = usersRepository.findOneByEmail(data.getEmail());

        if (checkUserExists == null) {
            throw new InvalidLoginException();
        }

        if (!checkUserExists.getPassword().equals(data.getPassword())) {
            throw new InvalidLoginException();
        }

        return AuthenticationService.createToken(checkUserExists);
    }
}
