package ru.tskmngr.task_manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tskmngr.task_manager.models.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
