package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.ArrayEntry;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArrayEntryRepository extends JpaRepository<ArrayEntry, Long> {
    ArrayEntry findByArrayEntryId(long arrayEntryId);

    @Override
    void delete(ArrayEntry arrayEntry);

}
