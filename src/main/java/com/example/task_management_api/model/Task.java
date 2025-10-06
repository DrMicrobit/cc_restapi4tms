package com.example.task_management_api.model;

import java.time.Instant;
import java.util.UUID;


/**
 * Task record, representing a task in the task management system. Using Java Record for
 * immutability and conciseness. Timestamps are in UTC.
 * 
 * @implNote During write-up of "what I learned" (see AIusage.md) and going through the initial
 *           suggestion of Gemini and with the knowledge I gained here, I realized that my "fix" of
 *           Claude's suggestion (using ZonedDateTime instead of LocalDateTime) was not good enough.
 *           ZonedDateTime is apparently not the way to go. The correct type to represent a UTC
 *           timestamp is Instant. For future consideration.
 * 
 * 
 * @param id Unique identifier for the task.
 * @param title Title of the task.
 * @param author Author of the task.
 * @param project Project associated with the task.
 * @param status Status of the task (e.g., "open", "in progress", "closed").
 * @param description Description of the task.
 * @param createdAt Timestamp when the task was created (in UTC).
 * @param updatedAt Timestamp when the task was last updated (in UTC).
 */

public record Task(UUID id,
        String title, String author,
        String project, String status,
        String description,
        Instant createdAt,
        Instant updatedAt) {

    /*
     * Convenience constructor for creating a new Task with current timestamps in UTC.
     */
    public Task(UUID id, String title,
            String author,
            String project,
            String status,
            String description) {
        this(id, title, author, project, status, description,
                Instant.now(), Instant.now());
    }
}
