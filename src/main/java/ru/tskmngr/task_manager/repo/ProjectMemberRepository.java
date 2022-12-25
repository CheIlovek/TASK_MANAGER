package ru.tskmngr.task_manager.repo;

import org.springframework.data.repository.CrudRepository;
import ru.tskmngr.task_manager.models.ProjectUser;

import java.util.List;

public interface ProjectMemberRepository  extends CrudRepository<ProjectUser, Integer> {
    List<ProjectUser> findByUserIdAndProjectId(long userId, int projectId);
}
