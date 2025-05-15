package org.abx.webappgen.creds.dao;


import org.abx.webappgen.creds.model.Page;
import org.abx.webappgen.creds.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageContentRepository extends JpaRepository<Page, Long> {
    Page findByPagename(String pagename);

    @Override
    void delete(Page user);

}
