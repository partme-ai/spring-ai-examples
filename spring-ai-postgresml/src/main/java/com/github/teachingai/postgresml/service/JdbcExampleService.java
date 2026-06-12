package com.github.partmeai.postgresml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * JDBC示例服务类
 * 演示如何使用JdbcTemplate进行数据库操作
 */
@Service
public class JdbcExampleService {

    private static final Logger logger = LoggerFactory.getLogger(JdbcExampleService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接
     * @return 数据库版本信息
     */
    public String testConnection() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT version()", String.class);
            logger.info("数据库连接成功，版本: {}", version);
            return version;
        } catch (Exception e) {
            logger.error("数据库连接测试失败", e);
            throw new RuntimeException("数据库连接测试失败", e);
        }
    }

    /**
     * 查询所有表名
     * @return 表名列表
     */
    public List<String> getAllTables() {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * 执行自定义查询
     * @param sql SQL语句
     * @return 查询结果列表
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            logger.error("执行查询失败: {}", sql, e);
            throw new RuntimeException("执行查询失败", e);
        }
    }

    /**
     * 执行更新操作
     * @param sql SQL语句
     * @param args 参数
     * @return 影响的行数
     */
    @Transactional
    public int executeUpdate(String sql, Object... args) {
        try {
            int rows = jdbcTemplate.update(sql, args);
            logger.info("执行更新成功，影响行数: {}", rows);
            return rows;
        } catch (Exception e) {
            logger.error("执行更新失败: {}", sql, e);
            throw new RuntimeException("执行更新失败", e);
        }
    }

    /**
     * 批量执行操作
     * @param sql SQL语句
     * @param batchArgs 批量参数
     * @return 影响的行数数组
     */
    @Transactional
    public int[] executeBatchUpdate(String sql, List<Object[]> batchArgs) {
        try {
            int[] results = jdbcTemplate.batchUpdate(sql, batchArgs);
            logger.info("批量执行成功，影响行数: {}", results.length);
            return results;
        } catch (Exception e) {
            logger.error("批量执行失败: {}", sql, e);
            throw new RuntimeException("批量执行失败", e);
        }
    }
}
