package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    Component findBycomponentId(long componentId);

    @Override
    void delete(Component user);

}
