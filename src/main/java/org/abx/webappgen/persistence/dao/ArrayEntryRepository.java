package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayEntry;
import org.abx.webappgen.persistence.model.ArrayResource;
import org.abx.webappgen.persistence.model.MapEntry;
import org.abx.webappgen.persistence.model.MapResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArrayEntryRepository extends JpaRepository<ArrayEntry, Long> {
    ArrayEntry findByArrayEntryId(long arrayEntryId);

    @Override
    void delete(ArrayEntry arrayEntry);


    // Paging support: fetch a page of ArrayResource
    Page<ArrayEntry> findByArrayResource(ArrayResource mapResource, Pageable pageable);


    long countByArrayResource(ArrayResource arrayResource);
}
