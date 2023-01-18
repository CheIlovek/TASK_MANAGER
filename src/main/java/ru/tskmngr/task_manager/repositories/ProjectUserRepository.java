package ru.tskmngr.task_manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tskmngr.task_manager.models.ProjectUser;
import ru.tskmngr.task_manager.models.composite_keys.ProjectUserId;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, ProjectUserId> {
    ProjectUser findByUserIdAndProjectId(long userId, long projectId);
    List<ProjectUser> findAllByUserId(long userId);
    List<ProjectUser> findAllByProjectId(long projectId);
    void deleteAllByProjectId(long projectId);
}
