package com.example.task_management_api.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.example.task_management_api.model.Task;

// xTODO: guarantees and docs

@Repository
public class TaskRepositoryInMemory implements TaskRepository {

    // Create
    @Override
    public Task create(Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    // Read
    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findByStatus(String status) {
        return tasks.values().stream()
                .filter(task -> status.equals(task.status()))
                .toList();
    }

    // Update
    // nothing atm

    // Delete
    public void clear() {
        tasks.clear();
    }

    // Query

    public boolean existsByTitleAndAuthor(String title, String author) {
        return tasks.values().stream().anyMatch(
                task -> title.equals(task.title()) && author.equals(task.author()));
    }


    // Container-like convenience functions

    @Override
    public long count() {
        return tasks.size();
    }

    @Override
    public boolean isEmpty() {
        return tasks.isEmpty();
    }


    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------

    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();
}
