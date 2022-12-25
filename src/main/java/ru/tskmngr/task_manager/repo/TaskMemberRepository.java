package ru.tskmngr.task_manager.repo;

import org.springframework.data.repository.CrudRepository;
import ru.tskmngr.task_manager.models.TaskUser;

public interface TaskMemberRepository  extends CrudRepository<TaskUser, Integer> {
}
