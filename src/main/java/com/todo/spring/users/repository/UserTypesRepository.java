package com.todo.spring.users.repository;

import com.todo.spring.users.models.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypesRepository extends JpaRepository<UserType, Long> {
    UserType findOneByName(String name);
}
