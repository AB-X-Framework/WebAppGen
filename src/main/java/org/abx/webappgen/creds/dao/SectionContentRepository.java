package org.abx.webappgen.creds.dao;


import org.abx.webappgen.creds.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionContentRepository extends JpaRepository<Component, Long> {
    Component findBycomponentId(long componentId);

    @Override
    void delete(Component user);

}
