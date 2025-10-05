package com.example.task_management_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.task_management_api.model.Task;

// Interface for basic repository with CRUD capabilities
// Additional convenience functions like known from containers (clear(), isEmpty(), etc.)

// xTODO: decide whether existsByTitleAndAuthor here or in business logic
// Trending to "here" for simplicity atm, or else need to implement find with equality callbacks

public interface TaskRepository {

    // Create

    Task create(Task task);


    // Read

    List<Task> findAll();

    Optional<Task> findById(UUID id);

    List<Task> findByStatus(String status);


    // Update
    // nothing atm

    // Delete
    void clear();

    void deleteById(UUID id);


    // Special query functions

    boolean existsByTitleAndAuthor(String title, String author);


    // Status functions regarding repository like known from conatiners

    long count();

    boolean isEmpty();
}
