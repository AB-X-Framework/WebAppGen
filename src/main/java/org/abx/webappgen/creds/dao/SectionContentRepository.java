package org.abx.webappgen.creds.dao;


import org.abx.webappgen.creds.model.Page;
import org.abx.webappgen.creds.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionContentRepository extends JpaRepository<Section, Long> {
    Section findBySectionId(long sectionId);

    @Override
    void delete(Section user);

}
