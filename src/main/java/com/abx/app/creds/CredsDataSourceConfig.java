package com.abx.app.creds;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.abx.app.creds.dao",
        entityManagerFactoryRef = "credsEntityManagerFactory"
)
public class CredsDataSourceConfig {


    @Bean(name = "credsEntityManagerFactory")
    @ConfigurationProperties(prefix = "spring.datasource.creds.jpa")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages("com.web.app.creds.model") // Your entity package
                .build();
    }


}