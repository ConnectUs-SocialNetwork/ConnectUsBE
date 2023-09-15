package com.example.ConnectUs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.ConnectUs.repository.postgres", transactionManagerRef = "postgresTransactionManager")
@EnableTransactionManagement
public class PostgresConfiguration {
    @Bean(name = "postgres")
    @ConfigurationProperties(prefix = "spring.postgres")
    public DataSource dataSource(){
        return DataSourceBuilder.create().driverClassName("org.postgresql.Driver").build();
    }

    @Bean(name = "postgresTransactionManager")
    public JpaTransactionManager postgresTransactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) throws Exception{
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }
}
