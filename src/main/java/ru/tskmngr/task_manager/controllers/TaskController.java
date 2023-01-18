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
import ru.tskmngr.task_manager.repositories.*;
import ru.tskmngr.task_manager.service.ProjectService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.LinkedList;

@Controller
public class TaskController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskUserRepository taskUserRepository;
    @Autowired
    ProjectUserRepository PURepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    TaskUserRepository TURepository;



    @GetMapping("/my_tasks")
    public String getMyTasks(Principal principal, Model model) {
        User curUser = userRepository.findByUsername(principal.getName());
        Iterable<Task> tasks = projectService.getTasksByUserId(curUser.getId());
        LinkedList<ShowTask> showTasks = new LinkedList<>();
        for (Task task : tasks) {
            // TODO а если менеджер смотрит "свои таски"?
            Iterable<User> members = projectService.getMembersByTaskId(task.getId());
            showTasks.add(new ShowTask(task, members));
        }
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        model.addAttribute("admin",isAdmin);
        model.addAttribute("tasks",showTasks);
        return "/my_tasks";
    }

    @GetMapping("/project/tasks/get_task")
    public String getTask(
            @RequestParam(value = "project_id",required = false) String projectId,
            @RequestParam(required = false,value = "task_id") String taskId,
            Principal principal,Model model) {
        if (projectId == null && taskId != null) {
            long tskId = Long.parseLong(taskId);
            User curUser = userRepository.findByUsername(principal.getName());
            Task task = taskRepository.findById(tskId);
            ProjectUser projectUser =
                    PURepository.findByUserIdAndProjectId(curUser.getId(),task.getProject().getId());
            if (projectUser == null)
                return "redirect:/home";
            TaskUser taskUser = new TaskUser(
                    task.getId(),
                    curUser.getId(),
                    "WORKER"
            );
            TURepository.save(taskUser);
            task.setStatus(1);
            taskRepository.save(task);
            return "redirect:/project/tasks?project_id=" + task.getProject().getId();
        }
        if (projectId != null && taskId == null) {
            long prjId = Long.parseLong(projectId);
            User curUser = userRepository.findByUsername(principal.getName());
            ProjectUser projectUser = PURepository.findByUserIdAndProjectId(curUser.getId(),prjId);
            if (projectUser == null)
                return "redirect:/home";
            Iterable<Task> tasks = projectService.getAvailableTasks(prjId,curUser.getId());
            if (tasks == null)
                return "redirect:/home"; //TODO надо писать NO AVAILABLE TASKS
            Project project = projectRepository.findById(prjId);
            LinkedList<ShowTask> showTasks = new LinkedList<>();
            for (Task task : tasks) {
                if (TURepository.findByUserIdAndTaskId(curUser.getId(),task.getId()) == null) {
                    Iterable<User> members = projectService.getMembersByTaskId(task.getId());
                    showTasks.add(new ShowTask(task, members));
                }
            }
            model.addAttribute("project",project);
            model.addAttribute("tasks",showTasks);
            boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
            model.addAttribute("admin",isAdmin);
            return "/project/get_task";
        }
        return "redirect:/home";
    }

    @GetMapping("/project/tasks")
    public String projectTasks(@RequestParam("project_id") String projectId, Principal principal, Model model) {
        long prjId = Long.parseLong(projectId);
        User curUser = userRepository.findByUsername(principal.getName());
        ProjectUser projectUser = PURepository.findByUserIdAndProjectId(curUser.getId(),prjId);

        Iterable<Task> tasks = null;
        if (curUser.getAuthority().getAuthority().equals("ROLE_ADMIN")) {
            tasks = taskRepository.findAllByProjectId(prjId);
        } else {
            switch (projectUser.getRole()) {
                case "OWNER", "MANAGER" -> tasks = taskRepository.findAllByProjectId(prjId);
                case "WORKER" -> tasks = projectService.getTasksByProjectIdAndUserId(prjId, curUser.getId());
            }
        }
        if (tasks == null)
            return "redirect:/home";
        Project project = projectRepository.findById(prjId);
        LinkedList<ShowTask> showTasks = new LinkedList<>();
        for (Task task : tasks) {
            Iterable<User> members = projectService.getMembersByTaskId(task.getId());
            showTasks.add(new ShowTask(task,members));
        }
        if (curUser.getAuthority().getId() == 1)
            model.addAttribute("role","OWNER");
        else
            model.addAttribute("role",projectUser.getRole());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        model.addAttribute("admin",isAdmin);
        model.addAttribute("project",project);
        model.addAttribute("tasks",showTasks);
        return "/project/tasks";
    }


    @GetMapping("/project/create_task")
    public String createProject(@RequestParam("project_id") String projectId, Principal principal, Model model) {
        User curUser = userRepository.findByUsername(principal.getName());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        model.addAttribute("admin",isAdmin);
        model.addAttribute("project_id",projectId);
        return "/project/create_task";
    }

    @PostMapping("/project/create_task")
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
        return "redirect:/project/tasks?project_id=" + task.getProject().getId();
    }

    class ShowTask {
        private Task task;
        private Iterable<User> members;


        public ShowTask(Task task, Iterable<User> members) {
            this.task = task;
            this.members = members;

        }


        public ShowTask() {}

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Iterable<User> getMembers() {
            return members;
        }

        public void setMembers(Iterable<User> members) {
            this.members = members;
        }
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
