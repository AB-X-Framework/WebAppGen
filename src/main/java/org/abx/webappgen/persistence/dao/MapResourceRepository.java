package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.Component;
import org.abx.webappgen.persistence.model.MapEntry;
import org.abx.webappgen.persistence.model.MapResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MapResourceRepository extends JpaRepository<MapResource, Long> {
    MapResource findByMapResourceId(long mapResourceId);

    List<MapResource> findAllByPackageName(String name);

    @Override
    void delete(MapResource arrayEntry);

    @Query("SELECT DISTINCT c.packageName FROM MapResource c")
    List<String> findDistinctPackageNames();
}
