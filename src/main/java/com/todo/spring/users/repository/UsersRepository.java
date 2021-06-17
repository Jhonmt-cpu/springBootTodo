package com.todo.spring.users.repository;

import com.todo.spring.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {
    User findOneByEmail(String email);
}
