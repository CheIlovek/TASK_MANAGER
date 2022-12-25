package ru.tskmngr.task_manager.repo;

import org.springframework.data.repository.CrudRepository;
import ru.tskmngr.task_manager.models.Authority;

public interface AuthorityRepository  extends CrudRepository<Authority, Integer> {
}
