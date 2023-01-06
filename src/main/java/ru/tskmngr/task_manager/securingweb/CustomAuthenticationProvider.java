package ru.tskmngr.task_manager.securingweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.tskmngr.task_manager.models.User;
import ru.tskmngr.task_manager.repo.UserRepository;

// TODO ШИФРОВАНИЕ
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository dao;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        //получаем пользователя
        User user = dao.findByUsername(username);
        //смотрим, найден ли пользователь в базе
        if (user == null) {
            throw new BadCredentialsException("Unknown user " + username);
        }
        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Bad password");
        }
        UserDetails principal =
                new User(user.getUsername(), user.getPassword(), user.getAuthority());
        return new UsernamePasswordAuthenticationToken(
                principal, password, principal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

