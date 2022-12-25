package ru.tskmngr.task_manager.securingweb;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    DataSource dataSource;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .passwordEncoder(NoOpPasswordEncoder.getInstance())
//                .dataSource(dataSource)
//                .usersByUsernameQuery("select username,password,'true' "
//                        + "from user "
//                        + "where username = ?")
//                .authoritiesByUsernameQuery("SELECT username,authority FROM " +
//                        "(SELECT username,id from user where username = ?) as t1 " +
//                        "LEFT JOIN authority ON t1.id = authority.user_id");
//
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .csrf()
//                .disable()
                .authorizeRequests()
                .antMatchers("/registration/login").not().fullyAuthenticated()
                .antMatchers("/main_page", "/index","/table_page","/сreate_project").hasAnyRole("ADMIN","USER")
                //.antMatchers("/").permitAll()
                .antMatchers("/admin/*").hasRole("ADMIN")
                .antMatchers("/", "/css/*").permitAll()
                //Все остальные страницы требуют аутентификации
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/registration/login")
                .defaultSuccessUrl("/home")
                .failureUrl("/registration/login?error=true")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/");
    }

//    @Autowired
//    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
//    }
}
