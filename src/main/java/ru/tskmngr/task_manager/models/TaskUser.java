package ru.tskmngr.task_manager.models;

import ru.tskmngr.task_manager.models.composite_keys.TaskUserId;

import javax.persistence.*;

@Entity(name = "task_user")
@IdClass(TaskUserId.class)
public class TaskUser {
    @Id
    private long taskId;
    @Id
    private long userId;

    private String role;

    public TaskUser(long taskId, long userId, String role) {
        this.taskId = taskId;
        this.userId = userId;
        this.role = role;
    }

    public TaskUser() {}

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
