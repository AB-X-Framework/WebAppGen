package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Component;
import org.abx.webappgen.persistence.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    Page findByPageId(long pageId);

    Page findByMatchesId(long matchId);

    @Override
    void delete(Page user);

    List<Page> findAllByComponent(Component component);

    @Query("SELECT DISTINCT c.packageName FROM Page c")
    List<String> findDistinctPackageNames();


    List<Page> findAllByPackageName(String name);
}
