package com.example.task_management_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.task_management_api.model.Task;



/**
 * Repository interface for managing Task entities with CRUD operations and additional utility
 * methods.
 * 
 * Create, Read, Delete operations are supported along with methods to check existence, count, and
 * clear the repository.
 * 
 * @apiNote This interface does not include update operations for simplicity of toy project.
 * @apiNote existsByTitleAndAuthor is included here for simplicity, though it could (should?) also
 *          be part of business logic.
 */

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
