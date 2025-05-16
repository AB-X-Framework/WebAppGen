package org.abx.webappgen.persistence.dao;

import org.abx.webappgen.persistence.model.EnvValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnvValueRepository extends JpaRepository<EnvValue, Long> {
    EnvValue findEnvValueByEnvValueId(long envValueId);

    @Override
    void delete(EnvValue envValue);
}
