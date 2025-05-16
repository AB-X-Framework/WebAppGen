package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.BinaryResource;
import org.abx.webappgen.persistence.model.TextResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinaryResourceRepository extends JpaRepository<BinaryResource, Long> {
    BinaryResource findByBinaryResourceId(long componentId);

    @Override
    void delete(BinaryResource user);

}
