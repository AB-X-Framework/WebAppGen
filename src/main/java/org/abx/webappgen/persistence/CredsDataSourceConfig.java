package org.abx.webappgen.persistence;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.abx.webappgen.persistence.dao",
        entityManagerFactoryRef = "credsEntityManagerFactory"
)
public class CredsDataSourceConfig {


    @Bean(name = "credsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource,
            @Value("${spring.datasource.hbm2ddl.auto}") String ddlAuto) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", ddlAuto); // Ensures schema update
        return builder
                .dataSource(dataSource)
                .packages("org.abx.webappgen.persistence.model") // Your entity package
                .persistenceUnit("creds")
                .properties(properties)
                .build();
    }



}