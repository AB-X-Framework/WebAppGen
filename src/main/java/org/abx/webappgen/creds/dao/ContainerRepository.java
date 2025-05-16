package org.abx.webappgen.creds.dao;


import org.abx.webappgen.creds.model.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
    Container findByContainerComponentId(long containerId);

    @Override
    void delete(Container continer);

}
