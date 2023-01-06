package ru.tskmngr.task_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tskmngr.task_manager.models.Project;
import ru.tskmngr.task_manager.models.ProjectUser;
import ru.tskmngr.task_manager.models.Task;
import ru.tskmngr.task_manager.models.User;
import ru.tskmngr.task_manager.repo.*;
import ru.tskmngr.task_manager.service.ProjectService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.LinkedList;
import java.util.Optional;


// TODO ЕСЛИ РОЛЬ МЕНЕДЖЕРА ТО ВИДИТ ВСЕ ЗАДАНИЯ CHECK
// TODO И МОЖЕТ СОЗДАВАТЬ НОВЫЕ CHECK

// TODO ОСТАЛЬНЫЕ МОГУТ БРАТЬ НЕ ЗАВЕРШЕННЫЕ ЗАДАНИЯ
// TODO И ПОМЕЧАТЬ СВОИ КАК ВЫПОЛНЕННЫЕ

// TODO РАЗДАЧА РОЛЕЙ ПРИ СОЗДАНИИ ПРОЕКТА (MEGA HARDD??!?!?)

// TODO УДАЛЕНИЕ ПРОЕКТОВ

@Controller
public class ProjectController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectUserRepository projectUserRepository;
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectUserRepository PURepository;

    @Autowired
    ProjectService projectService;


    @GetMapping("/project/delete_project")
    public String deleteProject(@RequestParam("project_id") String projectId,Principal principal) {
        long prjId = Long.parseLong(projectId);
        String name = principal.getName();
        User curUser = userRepository.findByUsername(name);
        String role = PURepository.findByUserIdAndProjectId(curUser.getId(),prjId).getRole();
        if (role.equals("OWNER")) {
            projectService.deleteProject(prjId);
            return "redirect:/home";
        }
        return "redirect:/project/info?project_id=" + prjId;
    }

    @GetMapping("/project/create_project")
    public String createProject(Model model) {
        Iterable<User> users = userRepository.findAll();
        LinkedList<UserPublic> userPublics = new LinkedList<>();
        for (User user : users) {
            userPublics.add(new UserPublic(user.getUsername(),user.getId()));
        }
        model.addAttribute("users",userPublics);
        return "/project/create_project";
    }

    @PostMapping("/create_project")
    public String createProjectPost(@ModelAttribute("projectForm")@Valid ProjectForm projectForm,
                                    BindingResult bindingResult, Model model, Principal principal) {
        //TODO ДОБАВИТЬ УЧАСТНИКОВ ПРОЕКТА
        String name = principal.getName();
        User curUser = userRepository.findByUsername(name);

        if (bindingResult.hasErrors()) {
            model.addAttribute("error");
            return "/home";
        }

        Project project = new Project(
                projectForm.getTitle(),
                projectForm.getDescription()
        );
        projectRepository.save(project);
        ProjectUser projectOwner = new ProjectUser(
                project.getId(),
                curUser.getId(),
                "OWNER"
        );
        projectUserRepository.save(projectOwner);
        for (String strId : projectForm.getMembers()) {
            long id = Long.parseLong(strId);
            Optional<User> optionalUser = userRepository.findById(id);
            User member;
            if (optionalUser.isPresent()) {
                member = optionalUser.get();
                ProjectUser projectMember = new ProjectUser(
                        project.getId(),
                        member.getId(),
                        "WORKER"
                );
                projectUserRepository.save(projectMember);
            }
        }

        return "redirect:/project/info?project_id=" + project.getId();
    }

    @GetMapping("/project/info")
    public String projectInfo(@RequestParam("project_id") String projectId, Principal principal, Model model) {
        long prjId = Long.parseLong(projectId);
        User curUser = userRepository.findByUsername(principal.getName());
        String role =  PURepository.findByUserIdAndProjectId(curUser.getId(), prjId).getRole();
        Project project = projectRepository.findById(prjId);
        model.addAttribute("project",project);
        model.addAttribute("role",role);
        return "/project/info";
    }

    @GetMapping("/project/members")
    public String projectMembers(@RequestParam("project_id") String projectId, Principal principal, Model model) {
        long prjId = Long.parseLong(projectId);
        User curUser = userRepository.findByUsername(principal.getName());
        String role =  PURepository.findByUserIdAndProjectId(curUser.getId(), prjId).getRole();
        Project project = projectRepository.findById(prjId);

        Iterable<ProjectUser> projectUsers = PURepository.findAllByProjectId(prjId);
        LinkedList<ShowMember> showMembers = new LinkedList<>();
        for (ProjectUser projectUser : projectUsers) {
            Optional<User> user = userRepository.findById(projectUser.getUserId());
            user.ifPresent(value -> showMembers.add(
                    new ShowMember(value.getUsername(), projectUser.getRole())));
        }

        model.addAttribute("members",showMembers);
        model.addAttribute("role",role);
        return "/project/info";
    }

    @GetMapping("/project/tasks")
    public String projectTasks(@RequestParam("project_id") String projectId, Principal principal, Model model) {
        long prjId = Long.parseLong(projectId);
        User curUser = userRepository.findByUsername(principal.getName());
        ProjectUser projectUser = PURepository.findByUserIdAndProjectId(curUser.getId(),prjId);
        Iterable<Task> tasks = null;
        switch (projectUser.getRole()) {
            case "OWNER","MANAGER" ->
                tasks = taskRepository.findByProjectId(prjId);
            case "WORKER" ->
                tasks =  projectService.getTasksByProjectIdAndUserId(prjId,curUser.getId());
        }
        assert tasks != null; // TODO ВОЗМОЖНО НУЖНО КИДАТЬ НА "ВАМ НЕ ДОСТУПЕН ЭТОТ ПРОЕКТ"
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
        model.addAttribute("project",project);
        model.addAttribute("tasks",showTasks);
        return "/project/tasks";
    }

    class ShowMember {
        private String role,username;

        public ShowMember(String username, String role) {
            this.role = role;
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    class UserPublic {
        private String name;
        private long id;

        public UserPublic(String name, long id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    class ProjectForm {
        private String title,description;
        private String[] members;

        public ProjectForm(String title, String description, String[] members) {
            this.title = title;
            this.description = description;
            this.members = members;
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

        public String[] getMembers() {
            return members;
        }

        public void setMember(String[] members) {
            this.members = members;
        }
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

}