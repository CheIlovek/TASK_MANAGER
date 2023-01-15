package ru.tskmngr.task_manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tskmngr.task_manager.models.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(long projectId);
    Task findById(long taskId);

    List<Task> findAllByStatus(long status);
    List<Task> findAllByPriorityAndStatusGreaterThanAndProjectId(int priority,int status,long projectId);
    void deleteAllByProjectId(long projectId);

}
