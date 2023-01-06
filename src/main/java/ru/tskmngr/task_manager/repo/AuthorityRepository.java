package ru.tskmngr.task_manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tskmngr.task_manager.models.Authority;

public interface AuthorityRepository  extends JpaRepository<Authority, Long> {
}
