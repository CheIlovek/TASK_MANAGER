package ru.tskmngr.task_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tskmngr.task_manager.models.*;
import ru.tskmngr.task_manager.repo.*;
import ru.tskmngr.task_manager.service.ProjectService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class TaskController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskUserRepository taskUserRepository;

    @Autowired
    ProjectRepository projectRepository;


    @GetMapping("/creation/create_task")
    public String createProject(@RequestParam("project_id") String projectId, Model model) {
        model.addAttribute("project_id",projectId);
        return "/creation/create_task";
    }

    @PostMapping("/creation/create_task")
    public String createProjectPost(@ModelAttribute("TaskForm") @Valid TaskForm taskForm,
                                    BindingResult bindingResult, Model model, Principal principal) {
        String name = principal.getName();
        User curUser = userRepository.findByUsername(name);

        if (bindingResult.hasErrors()) {
            model.addAttribute("error");
            return "/home";
        }
        int priority = 1;
        switch (taskForm.getPriority()) {
            case "HIGH" -> priority = 0;
            case "LOW" -> priority = 2;
        }

        Task task = new Task(
                projectRepository.findById(taskForm.getProjectId()),
                taskForm.getTitle(),
                taskForm.getDescription(),
                taskForm.getStatus(),
                priority
        );

        taskRepository.save(task);
        TaskUser taskOwner = new TaskUser(
                task.getId(),
                curUser.getId(),
                "OWNER"
        );
        taskUserRepository.save(taskOwner);
        return "redirect:/project?project_id=" + task.getProject().getId();
    }


    class TaskForm {
        private String title, description, priority;
        private int status;
        private long projectId;

        public TaskForm(String title, String description, String status, String priority, String projectId) {
            this.title = title;
            this.description = description;
            this.status = Integer.parseInt(status);
            this.priority = priority;
            this.projectId = Long.parseLong(projectId);
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public long getProjectId() {
            return projectId;
        }

        public void setProjectId(long projectId) {
            this.projectId = projectId;
        }
    }
}
