package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageContentRepository extends JpaRepository<Page, Long> {
    Page findByPageId(long pageId);

    @Override
    void delete(Page user);

}
