package org.abx.webappgen.persistence.dao;


import org.abx.webappgen.persistence.model.Component;
import org.abx.webappgen.persistence.model.TextResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextResourceRepository extends JpaRepository<TextResource, Long> {
TextResourceRepository findByTextResourceId(long componentId);

    @Override
    void delete(TextResource user);

}
