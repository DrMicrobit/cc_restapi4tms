package com.example.task_management_api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;

// xTODO BaCh: decide/define guarantees (no null returns etc.), docs.
// xTODO BaCh: decide status ... checks here or somewhere in business layer? Trending toward BL,
// But then everything could/would there
// xTODO BaCh: getters/setters with jakarta annotations?
// xTODO BaCh: decide auto-update "updatedAt" in setters? Trending to "no"

// xTODO BaCh: check sonarqube for exception in num params for constructors?


public class Task {

    public Task( // NOSONAR: yes, I know, 8 parameters
            @NotBlank UUID id, @NotBlank(message = "Title is required") String title,
            @NotBlank(message = "Author is required") String author,
            @NotBlank(message = "Project is required") String project,
            @NotBlank(message = "Status is required") @Pattern(regexp = "^(pending|in|completed)$",
                    message = "Status must be 'pending', 'in-progress', or 'completed'.") String status,
            String description,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.status = status;
        this.project = project;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Convenience constructor, timestamps auto
    // xTODO: check: is java compiler smart enough to re-use the LocalDateTime call?
    public Task(@NotBlank UUID id, @NotBlank(message = "Title is required") String title,
            @NotBlank(message = "Author is required") String author,
            @NotBlank(message = "Project is required") String project,
            @NotBlank(message = "Status is required") @Pattern(
                    regexp = "^(pending|in-progress|completed)$",
                    message = "Status must be 'pending', 'in-progress', or 'completed'.") String status,
            String description) {
        this(id, title, author, project, status, description,
                LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(utcZoneId),
                LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(utcZoneId));
    }



    // Claude says: required for Spring.
    // xTODO: BaCh But then we'd have nulls in some fields. Hmmm. Test private once MVP runs.
    public Task() {}

    // Boilerplate getter/setter from VS Code

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ------------------------------------------------------------------------
    // Private section from here on
    // ------------------------------------------------------------------------

    private static ZoneId utcZoneId = ZoneId.of("UTC");

    // xTODO BaCh: decide on null vs ""

    @NotBlank
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    private String description;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(pending|in-progress|completed)$",
            message = "Status must be 'pending', 'in-progress', or 'completed'.")
    private String status;

    @NotBlank(message = "Project is required")
    private String project;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private ZonedDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private ZonedDateTime updatedAt;


}
