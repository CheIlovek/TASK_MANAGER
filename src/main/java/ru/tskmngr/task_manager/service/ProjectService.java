package ru.tskmngr.task_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tskmngr.task_manager.models.*;
import ru.tskmngr.task_manager.repo.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    UserRepository userRepo;
    @Autowired
    ProjectRepository projectRepo;
    @Autowired
    TaskRepository taskRepo;
    @Autowired
    ProjectUserRepository PURepo;

    @Autowired
    TaskUserRepository TURepo;

    public Iterable<Project> getProjectsByUserId(long userId) {
        ArrayList<ProjectUser> projectUserList =
                new ArrayList<>(PURepo.findAllByUserId(userId));
        LinkedList<Project> projects = new LinkedList<>();
        for (ProjectUser projectUser : projectUserList) {
            Project buff = projectRepo.findById(projectUser.getProjectId());
            projects.add(buff);
        }
        return projects;
    }



    public Iterable<Task> getTasksByProjectIdAndUserId(long projectId,long userId) {
        Iterable<Task> tasks = taskRepo.findByProjectId(projectId);
        LinkedList<Task> res = new LinkedList<>();
        for (Task task : tasks) {
            if (!TURepo.findByUserIdAndTaskId(userId, task.getId()).isEmpty())
                res.add(task);
        }

        return res;
    }

    public Iterable<User> getMembersByTaskId(long taskId) {
        Iterable<TaskUser> taskUsers = TURepo.findByTaskId(taskId);
        LinkedList<User> res = new LinkedList<>();
        for (TaskUser taskUser : taskUsers) {
           Optional<User> user = userRepo.findById(taskUser.getUserId());
            user.ifPresent(res::add);
        }
        return res;
    }

    public void deleteProject(long prjId) {
        projectRepo.deleteById(prjId);
        // TODO НАСТРОИТЬ CASCADE У ТАБЛИЦ
    }
}
