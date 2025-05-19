package org.abx.webappgen.persistence;


import org.abx.webappgen.persistence.dao.UserRepository;
import org.abx.webappgen.persistence.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class UserModel {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional
    public void createUserIfNotFound(final String username, final String password,
                                     final String role) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.username=(username);
            user.password=passwordEncoder.encode(password);
            user.enabled=true;
        }
        user.userId = PageModel.elementHashCode(username);
        user.role=role;
        userRepository.save(user);
    }

}