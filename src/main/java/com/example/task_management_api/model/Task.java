package com.example.task_management_api.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 *
 * Barebone record for storing tasks. No checks whatsoever as these are expected to be done by
 * business layer (TaskService) and jakarta annotations there. To be discussed in real production
 * environments.
 *
 * @apiNote All parameters are nullable atm and will also be given back as null.
 *
 * @param id id of the task
 * @param title
 * @param author
 * @param project
 * @param status
 * @param description
 * @param createdAt creation time, should be UTC!
 * @param updatedAt
 */

public record Task(UUID id,
        String title, String author,
        String project, String status,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") ZonedDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") ZonedDateTime updatedAt) {

    // Convenience constructor, timestamps auto
    // xTODO: check: is java compiler smart enough to re-use the LocalDateTime call?
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
