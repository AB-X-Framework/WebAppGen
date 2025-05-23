package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.TextResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TextResourceRepository extends JpaRepository<TextResource, Long> {
    TextResource findByTextResourceId(long textResourceId);

    @Override
    void delete(TextResource textResource);


    @Query("SELECT DISTINCT c.packageName FROM TextResource c")
    List<String> findDistinctPackageNames();

    List<TextResource> findAllByPackageName(String packageName);

}
