package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    Component findByComponentId(long componentId);

    List<Component> findAllByPackageName(String name);

    @Override
    void delete(Component user);

    @Query("SELECT DISTINCT c.packageName FROM Component c")
    List<String> findDistinctPackageNames();
}
