package ru.tskmngr.task_manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.tskmngr.task_manager.models.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(long projectId);
    void deleteAllByProjectId(long projectId);
}
