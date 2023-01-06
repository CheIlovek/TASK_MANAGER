package ru.tskmngr.task_manager.models;

import ru.tskmngr.task_manager.models.composite_keys.ProjectUserId;
import ru.tskmngr.task_manager.models.composite_keys.TaskUserId;

import javax.persistence.*;

@Entity(name = "project_user")
@IdClass(ProjectUserId.class)
public class ProjectUser {

    @Id
    private long projectId;
    @Id
    private long userId;
    private String role;

    public ProjectUser(long projectId, long userId,String role) {
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
    }
    public ProjectUser() {}

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
