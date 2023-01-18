package ru.tskmngr.task_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tskmngr.task_manager.models.*;
import ru.tskmngr.task_manager.repositories.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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


    public List<Task> getAvailableTasks(long projectId,long userId) {
        List<Task> tasks = taskRepo.findAllByPriorityAndStatusGreaterThanAndProjectId(0,0,projectId);
        boolean isAvailable = true;
        for (Task task : tasks) {
            if (TURepo.findByUserIdAndTaskId(userId,task.getId()) != null) {
                isAvailable = false;
            }
        }
        if (!tasks.isEmpty() && isAvailable)
            return tasks;
        isAvailable = true;
        tasks = taskRepo.findAllByPriorityAndStatusGreaterThanAndProjectId(1,0,projectId);
        for (Task task : tasks) {
            if (TURepo.findByUserIdAndTaskId(userId,task.getId()) != null) {
                isAvailable = false;
            }
        }
        if (!tasks.isEmpty() && isAvailable)
            return tasks;
        isAvailable = true;
        tasks = taskRepo.findAllByPriorityAndStatusGreaterThanAndProjectId(2,0,projectId);
        for (Task task : tasks) {
            if (TURepo.findByUserIdAndTaskId(userId,task.getId()) != null) {
                isAvailable = false;
            }
        }
        if (!tasks.isEmpty() && isAvailable)
            return tasks;
        return null;
    }

    public Iterable<Task> getTasksByProjectIdAndUserId(long projectId,long userId) {
        Iterable<Task> tasks = taskRepo.findAllByProjectId(projectId);
        LinkedList<Task> res = new LinkedList<>();
        for (Task task : tasks) {
            TaskUser taskUser = TURepo.findByUserIdAndTaskId(userId, task.getId());
            if (taskUser != null) {
                if (taskUser.getRole().equals("WORKER")) {
                    task.setStatus(1);
                }
                if (taskUser.getRole().equals("COMPLETED")) {
                    task.setStatus(0);
                }
                res.add(task);
            }

        }

        return res;
    }

    public Iterable<Task> getTasksByUserId(long userId) {
        Iterable<TaskUser> taskUsers = TURepo.findAllByUserId(userId);
        LinkedList<Task> tasks = new LinkedList<>();
        for (TaskUser taskUser : taskUsers) {
            Task task = taskRepo.findById(taskUser.getTaskId());
//            if (taskUser.getRole().equals("WORKER")) {
//                task.setStatus(1);
//            }
            if (taskUser.getRole().equals("COMPLETED")) {
                task.setStatus(0);
            }
            tasks.add(task);
        }

        return tasks;
    }


    public Iterable<User> getMembersByTaskId(long taskId) {
        Iterable<TaskUser> taskUsers = TURepo.findByTaskId(taskId);
        LinkedList<User> res = new LinkedList<>();
        for (TaskUser taskUser : taskUsers) {
            Optional<User> user = userRepo.findById(taskUser.getUserId());
            if (user.isPresent()) {
                User realUser = user.get();
                res.add(new User(realUser.getUsername(),null,null));
            }
        }
        return res;
    }

    @Transactional
    public void deleteProject(long prjId) {
        Iterable<Task> tasks = taskRepo.findAllByProjectId(prjId);
        for (Task task : tasks) {
            TURepo.deleteAllByTaskId(task.getId());
        }
        taskRepo.deleteAllByProjectId(prjId);
        PURepo.deleteAllByProjectId(prjId);
        projectRepo.deleteById(prjId);
    }


    @Transactional
    public void deleteWorkerFromTasks(long userId) {
        List<TaskUser> taskUsers = TURepo.findAllByUserId(userId);
        for (TaskUser taskUser : taskUsers) {
            TURepo.delete(taskUser);
            List<TaskUser> taskUsers1 = TURepo.findByTaskId(taskUser.getTaskId());
            if (taskUsers1 == null || taskUsers1.size() < 2) {
                Task task = taskRepo.findById(taskUser.getTaskId());
                task.setStatus(2);
                taskRepo.save(task);
            }
        }
    }


    @Transactional
    public void deleteWorkerFromTasks(long userId,long prjId) {
        Project project = projectRepo.findById(prjId);
        List<TaskUser> taskUsers = TURepo.findAllByUserId(userId);
        for (TaskUser taskUser : taskUsers) {
            if (taskRepo.findById(taskUser.getTaskId()).getProject().equals(project)) {
                TURepo.delete(taskUser);
                List<TaskUser> taskUsers1 = TURepo.findByTaskId(taskUser.getTaskId());
                if (taskUsers1 == null || taskUsers1.size() < 2) {
                    Task task = taskRepo.findById(taskUser.getTaskId());
                    task.setStatus(2);
                    taskRepo.save(task);
                }
            }
        }
    }

    @Transactional
    public void deleteWorkerFromProject(long userId) {
        List<ProjectUser> projectUsers = PURepo.findAllByUserId(userId);
        for (ProjectUser projectUser : projectUsers) {
            PURepo.delete(projectUser);
            if (PURepo.findAllByProjectId(projectUser.getProjectId()) == null) {
                projectRepo.deleteById(projectUser.getProjectId());
            }
        }
    }
}
