package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Component;
import org.abx.webappgen.persistence.model.InnerComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InnerComponentRepository extends JpaRepository<InnerComponent, Long> {
    InnerComponent findByInnerComponentId(long innerComponentId);

    List<InnerComponent> findAllByChild(Component child);

    @Override
    void delete(InnerComponent innerComponent);

}
