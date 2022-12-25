package ru.tskmngr.task_manager.repo;

import org.springframework.data.repository.CrudRepository;
import ru.tskmngr.task_manager.models.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findByUsername(String username);
}
