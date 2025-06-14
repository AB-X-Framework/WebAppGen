package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayResource;
import org.abx.webappgen.persistence.model.BinaryResource;
import org.abx.webappgen.persistence.model.MapResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ArrayResourceRepository extends JpaRepository<ArrayResource, Long> {
    ArrayResource findByArrayResourceId(long arrayResourceId);

    List<ArrayResource> findAllByPackageName(String name);

    @Override
    void delete(ArrayResource user);

    @Query("SELECT DISTINCT c.packageName FROM ArrayResource c")
    List<String> findDistinctPackageNames();
}
