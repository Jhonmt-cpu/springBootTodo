package com.todo.spring.modules.todos.repositories;

import com.todo.spring.modules.todos.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findByTitleContainingIgnoreCase(String title);
}
