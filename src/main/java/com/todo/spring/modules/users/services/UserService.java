package com.todo.spring.modules.users.services;

import com.todo.spring.modules.users.dtos.AuthenticateUserDTO;
import com.todo.spring.modules.users.dtos.CreateUserDTO;
import com.todo.spring.modules.users.exceptions.InvalidLoginException;
import com.todo.spring.modules.users.exceptions.UserAlreadyExistsException;
import com.todo.spring.modules.users.exceptions.UserNotFoundException;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service()
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    private static final String key = "e85267357e1786c1c396743bccd4dfe5";

    long expirationMilis = 8600000L;

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
        User checkUserAlreadyExists = usersRepository.findOneByEmail(data.getEmail());

        if (checkUserAlreadyExists == null) {
            throw new InvalidLoginException();
        }

        if (!checkUserAlreadyExists.getPassword().equals(data.getPassword())) {
            throw new InvalidLoginException();
        }

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + expirationMilis);

        return Jwts.builder()
                .setIssuedAt(now)
                .setSubject("Teste testado")
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }
}
