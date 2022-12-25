package ru.tskmngr.task_manager.models;

import javax.persistence.*;

@Entity(name = "project_member")
public class ProjectUser {

    @Id
    private long projectId;
    @Id
    private long userId;
    private String role;

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
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
