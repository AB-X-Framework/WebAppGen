package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.BinaryResource;
import org.abx.webappgen.persistence.model.Component;
import org.abx.webappgen.persistence.model.TextResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BinaryResourceRepository extends JpaRepository<BinaryResource, Long> {
    BinaryResource findByBinaryResourceId(long componentId);

    List<BinaryResource> findAllByPackageName(String packageName);

    @Override
    void delete(BinaryResource user);

    @Query("SELECT DISTINCT c.packageName FROM BinaryResource c")
    List<String> findDistinctPackageNames();


    @Query("SELECT DISTINCT c.contentType FROM BinaryResource c")
    List<String> findDistinctContentTypes();
}
