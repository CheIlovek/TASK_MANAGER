package ru.tskmngr.task_manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.tskmngr.task_manager.models.Project;

import java.util.List;

public interface ProjectRepository  extends JpaRepository<Project, Integer> {
    Project findById(long projectId);
    void deleteById(long projectId);
}
