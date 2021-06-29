package com.todo.spring.shared.security;

import com.todo.spring.modules.users.dtos.UserAuthenticatedDTO;
import com.todo.spring.modules.users.exceptions.UserNotFoundException;
import com.todo.spring.modules.users.models.User;
import com.todo.spring.modules.users.repository.UsersRepository;
import com.todo.spring.shared.exceptions.InvalidJwtAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JwtTokenFilter extends GenericFilterBean {

    private final UsersRepository usersRepository;

    public JwtTokenFilter(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")){
            String token = authorization.replace("Bearer ", "");

            boolean tokenValid = AuthenticationService.validateToken(token);

            if (!tokenValid) {
                throw new InvalidJwtAuthenticationException();
            }

            String jws = AuthenticationService.getSubject(token);

            Optional<User> checkUserExists = usersRepository.findById(UUID.fromString(jws));

            if (checkUserExists.isEmpty()) {
                throw new UserNotFoundException();
            }

            User user = checkUserExists.get();

            UserAuthenticatedDTO userAuthenticated = UserAuthenticatedDTO.toDTO(user);

            List<SimpleGrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority("USER"));
            if (userAuthenticated.getTypeId() == 3) {
                roles.add(new SimpleGrantedAuthority("ADMIN"));
            }

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            userAuthenticated,
                            token,
                            roles
                    )
            );
        }

        filterChain.doFilter(req, res);
    }
}
