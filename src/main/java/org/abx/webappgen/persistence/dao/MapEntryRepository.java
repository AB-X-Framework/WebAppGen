package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayPairResource;
import org.abx.webappgen.persistence.model.MapEntry;
import org.abx.webappgen.persistence.model.MapResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapEntryRepository extends JpaRepository<MapEntry, Long> {

    // Find by ID (existing)
    MapEntry findByMapEntryId(long mapEntryId);

    // Delete by entity (inherited, but can override if needed)
    @Override
    void delete(MapEntry mapEntry);

    // Paging support: fetch a page of MapEntry
    Page<MapEntry> findByMapResource(MapResource mapResource, Pageable pageable);

    long countByMapResource(MapResource mapResource);

    List<MapEntry> findAllByMapResource(MapResource mapResource);
}
