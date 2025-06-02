package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Component;
import org.abx.webappgen.persistence.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    Page findByPageId(long pageId);

    Page findByMatchesId(long matchId);

    @Override
    void delete(Page user);

    List<Page> findAllByComponent(Component component);
}
