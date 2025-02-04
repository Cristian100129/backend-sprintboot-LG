package com.backend.apirest.squema;

import java.util.Date;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.backend.apirest.squema.TaskStatus;

@Document(collection = "tasks")
public class task {
    @Id
    private String id;
    private String title;
    private String description;
    private TaskStatus status; 

    @CreatedDate
    private Date createdAt;

    // Constructor vacío
    public task() {
    }

    // Constructor con parámetros (sin incluir createdAt, ya que se asignará automáticamente)
    public task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
