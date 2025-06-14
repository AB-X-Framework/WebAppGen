package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayEntry;
import org.abx.webappgen.persistence.model.ArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Array;


public interface ArrayEntryRepository extends JpaRepository<ArrayEntry, Long> {
    ArrayEntry findByArrayEntryId(long arrayEntryId);

    @Override
    void delete(ArrayEntry arrayEntry);


    // Paging support: fetch a page of ArrayResource
    java.util.ArrayList<ArrayEntry> findAllByArrayResourceId(long arrayResourceId);

    Page<ArrayEntry> findByArrayResourceId(long arrayResourceId, Pageable pageable);


    long countByArrayResourceId(long arrayResourceId);
}
