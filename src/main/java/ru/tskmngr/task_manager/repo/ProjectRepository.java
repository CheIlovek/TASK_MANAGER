package ru.tskmngr.task_manager.repo;

import org.springframework.data.repository.CrudRepository;
import ru.tskmngr.task_manager.models.Project;

public interface ProjectRepository  extends CrudRepository<Project, Integer> {
    Project findById(long projectId);
    void deleteById(long projectId);
}
