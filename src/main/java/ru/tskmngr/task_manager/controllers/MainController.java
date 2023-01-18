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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskUserRepository TURepo;

    @Autowired
    ProjectUserRepository PURepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    AuthorityRepository authorityRepository;
    @GetMapping("/")
    public String main() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        User curUser = userRepository.findByUsername(principal.getName());
        Iterable<Project> projects;
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        if (isAdmin)
            projects = projectRepository.findAll();
        else
            projects = projectService.getProjectsByUserId(curUser.getId());
        LinkedList<ShowProject> showProjects = new LinkedList<>();
        for (Project project : projects) {
            String role = isAdmin ? "ADMIN" : PURepository.findByUserIdAndProjectId(curUser.getId(),project.getId()).getRole();
            showProjects.add(new ShowProject(
                    project.getId(),
                    role,
                    project.getTitle()
            ));
        }

        model.addAttribute("admin",isAdmin);
        model.addAttribute("projects",showProjects);
        return "/home";
    }

    @GetMapping("/admin_panel")
    String adminPanel(Principal principal, Model model) {
        User curUser = userRepository.findByUsername(principal.getName());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        if (!isAdmin)
            return "redirect:/";
        List<Authority> roles =  authorityRepository.findAll();
        List<User> users = userRepository.findAll();
        model.addAttribute("admin",true);
        model.addAttribute("users",users);
        model.addAttribute("roles",roles);


        return "/admin_panel";
    }

    @PostMapping("/admin_panel/change_role")
    String changeRole(Principal principal,
                      @RequestParam(value = "newRole") String newRole,
                      @RequestParam(value = "userId") String userIdStr) {
        long userId = Long.parseLong(userIdStr);
        // TODO TEST
        User curUser = userRepository.findByUsername(principal.getName());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        if (!isAdmin)
            return "redirect:/";
        Optional<User> subject = userRepository.findById(userId);

        if (subject.isPresent()) {
            User user = subject.get();
            user.setAuthority(authorityRepository.findByAuthority(newRole));
            userRepository.save(user);
            if (Objects.equals(newRole, "ROLE_ADMIN")) {
                projectService.deleteWorkerFromTasks(userId);
            }
        }
        return "redirect:/admin_panel";
    }



    @PostMapping("/admin_panel/delete_user")
    String deleteUser(Principal principal, @RequestParam(value = "userId") String userIdStr) {
        long userId = Long.parseLong(userIdStr);
        User curUser = userRepository.findByUsername(principal.getName());
        boolean isAdmin = curUser.getAuthority().getAuthority().equals("ROLE_ADMIN");
        if (!isAdmin)
            return "redirect:/";
        projectService.deleteWorkerFromProject(userId);
        projectService.deleteWorkerFromTasks(userId);
        userRepository.deleteById(userId);
        return "redirect:/admin_panel";
    }


    public class ShowProject {

        private long id;
        private String role;
        private String title;

        public ShowProject(long id, String role, String title) {
            this.role = role;
            this.title = title;
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
