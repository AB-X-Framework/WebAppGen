package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    Component findByComponentId(long componentId);

    List<Component> findAllByPackageName(String name);

    @Override
    void delete(Component user);

}
