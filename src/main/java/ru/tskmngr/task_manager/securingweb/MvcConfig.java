package ru.tskmngr.task_manager.securingweb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/main_page").setViewName("/main_page");
        registry.addViewController("/index").setViewName("/index");
        registry.addViewController("/create_task").setViewName("/create_task");
        registry.addViewController("/create_project").setViewName("/create_project");
        //registry.addViewController("/registration/signup").setViewName("/registration/signup");
        //registry.addViewController("/registration/login").setViewName("/registration/login");
        registry.addViewController("/table_page").setViewName("/table_page");
    }

}
