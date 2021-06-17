package com.todo.spring.repository;

import com.todo.spring.models.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTypesRepository extends JpaRepository<UserType, UUID> {
}
