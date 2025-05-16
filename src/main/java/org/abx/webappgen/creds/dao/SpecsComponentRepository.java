package org.abx.webappgen.creds.dao;


import org.abx.webappgen.creds.model.Component;
import org.abx.webappgen.creds.model.SpecsComponent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecsComponentRepository extends JpaRepository<SpecsComponent, Long> {
    SpecsComponent findBySpecComponentId(long specsComponentId);

    @Override
    void delete(SpecsComponent specsComponent);

}
