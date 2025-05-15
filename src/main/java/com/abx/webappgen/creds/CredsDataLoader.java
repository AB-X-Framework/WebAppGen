package com.abx.webappgen.creds;


import com.abx.webappgen.creds.dao.UserRepository;
import com.abx.webappgen.creds.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CredsDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        // == create initial user
        createUserIfNotFound("abx@webappgen.com", "12345", "Admin");
        alreadySetup = true;
    }


    @Transactional
    public void createUserIfNotFound(final String username, final String password,
                                     final String role) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEnabled(true);
        }
        user.setRole(role);
        userRepository.save(user);
    }

}