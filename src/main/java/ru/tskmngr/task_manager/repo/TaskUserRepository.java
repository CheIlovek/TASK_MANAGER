package ru.tskmngr.task_manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tskmngr.task_manager.models.ProjectUser;
import ru.tskmngr.task_manager.models.TaskUser;
import ru.tskmngr.task_manager.models.composite_keys.TaskUserId;

import java.util.List;

public interface TaskUserRepository extends JpaRepository<TaskUser, TaskUserId> {
    List<TaskUser> findByUserIdAndTaskId(long userId, long taskId);
    List<TaskUser> findByUserId(long userId);
    List<TaskUser> findByTaskId(long taskId);
}
