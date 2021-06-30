package com.todo.spring.shared.security;

import com.todo.spring.modules.users.dtos.UserAuthenticatedDTO;
import com.todo.spring.modules.users.repository.UsersRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtTokenFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")){
            String token = authorization.replace("Bearer ", "");

            boolean tokenValid = AuthenticationService.validateToken(token);

            if (tokenValid) {
                UserAuthenticatedDTO user = AuthenticationService.getUser(token);

                List<SimpleGrantedAuthority> roles = new ArrayList<>();
                roles.add(new SimpleGrantedAuthority("ROLE_USER"));
//                if (user.getTypeId() == 3) {
//                    roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//                }

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                user,
                                token,
                                roles
                        )
                );
            }
        }
        filterChain.doFilter(req, res);
    }
}
