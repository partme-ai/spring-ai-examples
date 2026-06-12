package com.github.partmeai.postgresml.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * JDBC配置类
 * 用于初始化JdbcTemplate和相关数据库配置
 */
@Configuration
@EnableTransactionManagement
public class JdbcConfig {

    @Value("${spring.jdbc.template.query-timeout:30}")
    private int queryTimeout;

    /**
     * 配置JdbcTemplate
     * @param dataSource 数据源
     * @return JdbcTemplate实例
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setQueryTimeout(queryTimeout);
        return jdbcTemplate;
    }

    /**
     * 配置事务管理器
     * @param dataSource 数据源
     * @return PlatformTransactionManager实例
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
