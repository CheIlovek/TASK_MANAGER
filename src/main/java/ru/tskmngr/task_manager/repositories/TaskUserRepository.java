package ru.tskmngr.task_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tskmngr.task_manager.models.TaskUser;
import ru.tskmngr.task_manager.models.composite_keys.TaskUserId;

import java.util.List;

public interface TaskUserRepository extends JpaRepository<TaskUser, TaskUserId> {
    TaskUser findByUserIdAndTaskId(long userId, long taskId);
    List<TaskUser> findAllByUserId(long userId);

    List<TaskUser> findAllByTaskIdAndRole(long taskId,String role);
    List<TaskUser> findByTaskId(long taskId);
    void deleteAllByTaskId(long taskId);
}
