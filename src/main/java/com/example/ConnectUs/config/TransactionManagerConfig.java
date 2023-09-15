package com.example.ConnectUs.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
public class TransactionManagerConfig {
    @Bean(name = "chainedTransactionManager")
    @Primary
    public ChainedTransactionManager transactionManager (
            @Qualifier("postgresTransactionManager") PlatformTransactionManager postgreTransactionManager,
            @Qualifier("neo4jTransactionManager") PlatformTransactionManager neo4jTransactionManager) {
        return new ChainedTransactionManager(neo4jTransactionManager,
                postgreTransactionManager);
    }
}

