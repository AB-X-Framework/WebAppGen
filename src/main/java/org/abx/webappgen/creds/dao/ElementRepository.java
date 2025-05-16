package org.abx.webappgen.creds.dao;


import org.abx.webappgen.creds.model.Element;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElementRepository extends JpaRepository<Element, Long> {
    Element findByElementId(long elementId);

    @Override
    void delete(Element element);

}
