package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.ArrayEntry;
import org.abx.webappgen.persistence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByUserId(long username);

    @Override
    void delete(User user);

    Page<User> findAll( Pageable pageable);
}
