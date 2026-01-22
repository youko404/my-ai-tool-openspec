package com.ai.content.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.ai.content.repository.vector",
    entityManagerFactoryRef = "vectorEntityManagerFactory", transactionManagerRef = "vectorTransactionManager")
public class VectorJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean vectorEntityManagerFactory(EntityManagerFactoryBuilder builder,
        @Qualifier("vectorDataSource") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.format_sql", "true");
        return builder.dataSource(dataSource).packages("com.ai.content.domain.entity.vector").properties(properties)
            .persistenceUnit("vector").build();
    }

    @Bean
    public JpaTransactionManager vectorTransactionManager(
        @Qualifier("vectorEntityManagerFactory") LocalContainerEntityManagerFactoryBean vectorEntityManagerFactory) {
        return new JpaTransactionManager(vectorEntityManagerFactory.getObject());
    }
}
