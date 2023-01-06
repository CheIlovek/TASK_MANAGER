package ru.tskmngr.task_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.tskmngr.task_manager.models.Project;
import ru.tskmngr.task_manager.models.User;
import ru.tskmngr.task_manager.repo.ProjectUserRepository;
import ru.tskmngr.task_manager.repo.UserRepository;
import ru.tskmngr.task_manager.service.ProjectService;

import java.security.Principal;
import java.util.LinkedList;

@Controller
public class MainController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectUserRepository PURepository;
    @Autowired
    ProjectService projectService;
    @GetMapping("/")
    public String main() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        User curUser = userRepository.findByUsername(principal.getName());
        Iterable<Project> projects = projectService.getProjectsByUserId(curUser.getId());
        LinkedList<ShowProject> showProjects = new LinkedList<>();
        for (Project project : projects) {
            showProjects.add(new ShowProject(
                    project.getId(),
                    PURepository.findByUserIdAndProjectId(curUser.getId(),project.getId()).getRole(),
                    project.getTitle()
            ));
        }
        if (curUser.getAuthority().getId() == 1)
            model.addAttribute("admin",true);
        else
            model.addAttribute("admin",false);
        model.addAttribute("projects",showProjects);
        return "/home";
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
