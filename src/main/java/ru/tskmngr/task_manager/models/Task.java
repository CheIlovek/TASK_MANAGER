package ru.tskmngr.task_manager.models;

import javax.persistence.*;

@Entity(name = "task")
public class Task {



    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Project project;

    private String title,description;
    private int status,priority;

    public Task(Project project, String title, String description, int status, int priority) {
        this.project = project;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public Task() {}

    public Project getProject() {
        return project;
    }

    public void setProject(Project projectId) {
        this.project = projectId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
