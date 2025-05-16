package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
    Container findByContainerId(long containerId);

    @Override
    void delete(Container container);

}
