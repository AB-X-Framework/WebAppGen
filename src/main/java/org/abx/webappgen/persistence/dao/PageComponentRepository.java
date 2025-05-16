package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Page;
import org.abx.webappgen.persistence.model.PageComponent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageComponentRepository extends JpaRepository<PageComponent, Long> {
    PageComponent findByPageComponentId(long pageComponentId);

    @Override
    void delete(PageComponent user);

}
