package com.todo.spring.modules.users.repository;

import com.todo.spring.modules.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {
    User findOneByEmail(String email);
    Optional<User> findByUsername(String username);

}
