package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayEntry;
import org.abx.webappgen.persistence.model.MapEntry;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MapEntryRepository extends JpaRepository<MapEntry, Long> {
    MapEntry findByMapEntryId(long mapEntryId);

    @Override
    void delete(MapEntry arrayEntry);

}
