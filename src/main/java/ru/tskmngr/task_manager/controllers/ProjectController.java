package ru.tskmngr.task_manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.tskmngr.task_manager.models.Project;
import ru.tskmngr.task_manager.models.Task;
import ru.tskmngr.task_manager.models.User;
import ru.tskmngr.task_manager.repo.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class ProjectController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectMemberRepository projectMemberRepository;
    @Autowired
    ProjectRepository projectRepository;

    @GetMapping("/project")
    public ModelAndView project(HttpServletRequest request, Principal principal) {
        int prjId = Integer.parseInt(request.getParameter("prj_id"));
        String name = principal.getName();
        List<User> users = userRepository.findByUsername(name);
        if (!projectMemberRepository.findByUserIdAndProjectId(users.get(0).getId(),prjId).isEmpty()) {
            List<Task> tasks = taskRepository.findByProjectId(prjId);
            ModelAndView mav = new ModelAndView("/project");
            mav.addObject("tasks", tasks);
            return mav;
        }
        // НАДА АБРАБАТЫВААТЬ ЭЭЭЭ
        return null;
    }

    @PostMapping("/project/add")
    public String projectAdd(
            @RequestParam String title,@RequestParam String description,
            Principal principal) {
        //TODO ПЕРЕДЕЛАТЬ
        String name = principal.getName();
        List<User> users = userRepository.findByUsername(name);
        Project project = new Project(title,description);
        projectRepository.save(project);
        return "redirect:/project";
    }
}