package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.MethodSpec;
import org.abx.webappgen.persistence.model.TextResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MethodSpecRepository extends JpaRepository<MethodSpec, Long> {
    MethodSpec findByMethodSpecId(long methodSpecId);

    @Override
    void delete(MethodSpec methodSpec);

    @Query("SELECT DISTINCT c.packageName FROM MethodSpec c")
    List<String> findDistinctPackageNames();
}
