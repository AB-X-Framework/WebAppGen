package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayPairResource;
import org.abx.webappgen.persistence.model.ArrayResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ArrayPairResourceRepository extends JpaRepository<ArrayPairResource, Long> {
    ArrayPairResource findByArrayPairResourceId(long arrayPairResourceId);

    List<ArrayPairResource> findAllByPackageName(String name);

    @Override
    void delete(ArrayPairResource resource);

    @Query("SELECT DISTINCT c.packageName FROM ArrayPairResource c")
    List<String> findDistinctPackageNames();

    List<ArrayPairResource> findAllByArrayPairResourceId(Long arrayPairResourceId);
}
