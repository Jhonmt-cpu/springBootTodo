package com.todo.spring.modules.todos.repositories;

import com.todo.spring.modules.todos.models.TodoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TodoTypesRepository extends JpaRepository<TodoType, Long> {
    TodoType findOneByName(String name);
}

