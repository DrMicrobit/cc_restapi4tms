package com.example.task_management_api.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * Task record, representing a task in the task management system. Using Java Record for
 * immutability and conciseness. Timestamps are in UTC.
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") ZonedDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") ZonedDateTime updatedAt) {

    /*
     * Convenience constructor for creating a new Task with current timestamps in UTC.
     */
    public Task(UUID id, String title,
            String author,
            String project,
            String status,
            String description) {
        this(id, title, author, project, status, description,
                LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(utcZoneId),
                LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(utcZoneId));
    }


    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------

    // static ZoneId for conversion of local date to UTC by convenience constructor
    private static ZoneId utcZoneId = ZoneId.of("UTC");

}
