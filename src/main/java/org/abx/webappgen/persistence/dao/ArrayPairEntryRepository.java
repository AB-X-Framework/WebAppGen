package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayEntry;
import org.abx.webappgen.persistence.model.ArrayPairEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArrayPairEntryRepository extends JpaRepository<ArrayPairEntry, Long> {
    ArrayPairEntry findByArrayPairEntryId(long arrayEntryId);

    @Override
    void delete(ArrayPairEntry arrayPairEntry);


    // Paging support: fetch a page of ArrayResource
    java.util.ArrayList<ArrayPairEntry> findAllByArrayPairResourceId(long arrayPairResourceId);

    Page<ArrayPairEntry> findByArrayPairResourceId(long arrayPairResourceId, Pageable pageable);


    long countByArrayPairResourceId(long arrayResourceId);
}
