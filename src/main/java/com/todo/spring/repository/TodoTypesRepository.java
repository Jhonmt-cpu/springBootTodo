package com.todo.spring.repository;

import com.todo.spring.models.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Repository
@Service("todoTypesRepository")
public interface TodoTypesRepository extends JpaRepository<TodoType, UUID> {
    TodoType findOneByName(String name);
}

