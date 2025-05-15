package com.abx.app.creds.dao;


import com.abx.app.creds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Override
    void delete(User user);

}
