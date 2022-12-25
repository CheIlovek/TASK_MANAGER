package ru.tskmngr.task_manager.models.composite_keys;

import java.io.Serializable;

public class ProjectUserId implements Serializable {
    private long projectId;
    private long userId;

    public ProjectUserId(long projectId, long userId) {
        this.projectId = projectId;
        this.userId = userId;
    }
}
