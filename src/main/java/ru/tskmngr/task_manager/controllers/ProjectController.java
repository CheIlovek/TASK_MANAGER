package ru.tskmngr.task_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tskmngr.task_manager.models.*;
import ru.tskmngr.task_manager.repositories.*;
import ru.tskmngr.task_manager.service.ProjectService;

import java.security.Principal;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;


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
    TaskUserRepository TURepository;
    @Autowired
    ProjectService projectService;


    @GetMapping("/project/delete_project")
    public String deleteProject(@RequestParam("project_id") String projectId,Principal principal) {
        long prjId = Long.parseLong(projectId);
        String name = principal.getName();
        User curUser = userRepository.findByUsername(name);
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        String role = PURepository.findByUserIdAndProjectId(curUser.getId(),prjId).getRole();
        if (role.equals("OWNER") || isAdmin) {
            projectService.deleteProject(prjId);
            return "redirect:/home";
        }
        return "redirect:/project/info?project_id=" + prjId;
    }

    @GetMapping("/project/create_project")
    public String createProject(Model model,Principal principal) {
        Iterable<User> users = userRepository.findAll();
        LinkedList<UserPublic> userPublics = new LinkedList<>();
        for (User user : users) {
            if (!Objects.equals(principal.getName(), user.getUsername()))
                userPublics.add(new UserPublic(user.getUsername(),user.getId()));
        }
        User curUser = userRepository.findByUsername(principal.getName());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        model.addAttribute("admin",isAdmin);
        model.addAttribute("users",userPublics);
        return "/project/create_project";
    }

    @PostMapping("/project/create_project")
    public String createProjectPost(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("isManager") String[] managers,
            @RequestParam("member") String[] members,
            Model model, Principal principal) {
        String name = principal.getName();
        User curUser = userRepository.findByUsername(name);
        Project project = new Project(title, description);
        projectRepository.save(project);
        ProjectUser projectOwner = new ProjectUser(
                project.getId(),
                curUser.getId(),
                "OWNER"
        );
        projectUserRepository.save(projectOwner);
        LinkedList<Long> managerIds = new LinkedList<>();
        for (String manager : managers) {
            long val = Long.parseLong(manager);
            managerIds.add(val);
        }

        for (String idStr : members) {
            long id = Long.parseLong(idStr);
            Optional<User> optionalUser = userRepository.findById(id);
            User member;
            if (optionalUser.isPresent()) {
                member = optionalUser.get();
                ProjectUser projectMember = new ProjectUser(
                        project.getId(),
                        member.getId(),
                        managerIds.contains(id) ? "MANAGER" : "WORKER"
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
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        String role =  isAdmin ? "OWNER" : PURepository.findByUserIdAndProjectId(curUser.getId(), prjId).getRole();
        Project project = projectRepository.findById(prjId);
        model.addAttribute("project",project);
        model.addAttribute("role",role);
        model.addAttribute("admin",isAdmin);
        return "/project/info";
    }

    @GetMapping("/project/members")
    public String projectMembers(@RequestParam("project_id") String projectId, Principal principal, Model model) {
        //TODO Проверить пользователь может сюда заходить или нет
        long prjId = Long.parseLong(projectId);
        Project project = projectRepository.findById(prjId);

        Iterable<ProjectUser> projectUsers = PURepository.findAllByProjectId(prjId);
        LinkedList<ShowMember> showMembers = new LinkedList<>();
        for (ProjectUser projectUser : projectUsers) {
            Optional<User> user = userRepository.findById(projectUser.getUserId());
            user.ifPresent(value -> showMembers.add(
                    new ShowMember(value, projectUser.getRole())));
        }

        User curUser = userRepository.findByUsername(principal.getName());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        String role =  isAdmin ? "OWNER" : PURepository.findByUserIdAndProjectId(curUser.getId(), prjId).getRole();

        model.addAttribute("project",project);
        model.addAttribute("members",showMembers);
        model.addAttribute("role",role);
        model.addAttribute("admin",isAdmin);
        return "/project/members";
    }



    @GetMapping("/project/tasks/complete_task")
    public String completeTask(@RequestParam("task_id") String strTaskId,
                               Principal principal, Model model) {
        long taskId = Long.parseLong(strTaskId);
        User curUser = userRepository.findByUsername(principal.getName());
        TaskUser taskUser = TURepository.findByUserIdAndTaskId(curUser.getId(),taskId);
        Project project = taskRepository.findById(taskId).getProject();
        model.addAttribute("project",project);
        if (taskUser != null) {
            taskUser.setRole("COMPLETED");
            TURepository.save(taskUser);
        }
        else {
            return "redirect:/project/tasks?project_id=" + project.getId();
        }
        if (TURepository.findAllByTaskIdAndRole(taskId,"WORKER") == null) {
            Task task = taskRepository.findById(taskId);
            task.setStatus(0);
            taskRepository.save(task);
        }

        return "redirect:/project/tasks?project_id=" + project.getId();
    }


    @PostMapping("/project/delete_user")
    String deleteMember(Principal principal,
                        @RequestParam(value = "userId") String userIdStr,
                        @RequestParam(value = "prjId") String prjIdStr) {
        long userId = Long.parseLong(userIdStr);
        long prjId = Long.parseLong(prjIdStr);
        User curUser = userRepository.findByUsername(principal.getName());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        if (!isAdmin)
            return "redirect:/";
        projectService.deleteWorkerFromProject(userId);
        projectService.deleteWorkerFromTasks(userId,prjId);
        return "redirect:/project/members?project_id=" + prjId;
    }

    @PostMapping("/project/change_role")
    String changeRole(Principal principal,
                      @RequestParam(value = "newRole") String newRole,
                      @RequestParam(value = "userId") String userIdStr,
                      @RequestParam(value = "prjId") String prjIdStr) {
        long userId = Long.parseLong(userIdStr);
        long prjId = Long.parseLong(prjIdStr);
        ProjectUser projectUser = PURepository.findByUserIdAndProjectId(userId,prjId);
        projectUser.setRole(newRole);
        PURepository.save(projectUser);
        projectService.deleteWorkerFromTasks(userId);

        return "redirect:/project/members?project_id=" + prjId;
    }


    class ShowMember {
        private String role, username;
        private User user;

        public ShowMember(User user, String role) {
            this.role = role;
            this.user = user;
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

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
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




}