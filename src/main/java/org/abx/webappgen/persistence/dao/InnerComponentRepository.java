package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.InnerComponent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InnerComponentRepository extends JpaRepository<InnerComponent, Long> {
    InnerComponent findByInnerComponentId(long innerComponentId);

    @Override
    void delete(InnerComponent innerComponent);

}
