package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayResource;
import org.abx.webappgen.persistence.model.BinaryResource;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArrayResourceRepository extends JpaRepository<ArrayResource, Long> {
    ArrayResource findByArrayResourceId(long arrayResourceId);

    @Override
    void delete(ArrayResource user);

}
