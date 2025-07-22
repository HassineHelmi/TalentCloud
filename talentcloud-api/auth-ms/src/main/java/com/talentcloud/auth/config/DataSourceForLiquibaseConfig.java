package com.talentcloud.auth.config; // Or your main package

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceForLiquibaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceForLiquibaseConfig.class);


    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties liquibaseDataSourceProperties() {
        logger.info(">>>> Creating DataSourceProperties for Liquibase <<<<");
        return new DataSourceProperties();
    }


    @Bean
    @LiquibaseDataSource
    public DataSource liquibaseDataSource(
            @Qualifier("liquibaseDataSourceProperties") DataSourceProperties liquibaseDataSourceProperties) {
        logger.info(">>>> Creating HikariDataSource for Liquibase with URL: {} <<<<", liquibaseDataSourceProperties.getUrl());
        DataSource ds = liquibaseDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class) // Explicitly using HikariCP
                .build();
        if (ds instanceof HikariDataSource) {
            ((HikariDataSource) ds).setPoolName("LiquibaseHikariPool");

        }
        return ds;
    }
}