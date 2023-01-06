package ru.tskmngr.task_manager.models.composite_keys;

import java.io.Serializable;

public class TaskUserId implements Serializable {
    private long taskId;
    private long userId;

    public TaskUserId(long taskId, long userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public TaskUserId() {}
    // equals() and hashCode()
}