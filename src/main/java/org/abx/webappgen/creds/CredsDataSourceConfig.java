package org.abx.webappgen.creds;




import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.abx.webappgen.creds.dao",
        entityManagerFactoryRef = "credsEntityManagerFactory",
        transactionManagerRef = "credsTransactionManager"
)
public class CredsDataSourceConfig {

    @Bean(name = "credsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.creds")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "credsEntityManagerFactory")
    @ConfigurationProperties(prefix = "spring.datasource.creds.jpa")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("credsDataSource") DataSource dataSource,
            @Value("${spring.datasource.creds.hbm2ddl.auto}") String ddlAuto) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", ddlAuto); // Ensures schema update
        return builder
                .dataSource(dataSource)
                .packages("org.abx.webappgen.creds.model") // Your entity package
                .persistenceUnit("creds")
                .properties(properties)
                .build();
    }

    @Bean(name = "credsTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("credsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }


}