package com.github.partmeai.postgresml.controller;

import com.github.partmeai.postgresml.service.JdbcExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC控制器
 * 提供JdbcTemplate相关的REST API接口
 */
@RestController
@RequestMapping("/api/jdbc")
public class JdbcController {

    @Autowired
    private JdbcExampleService jdbcExampleService;

    /**
     * 测试数据库连接
     * @return 数据库版本信息
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            String version = jdbcExampleService.testConnection();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "数据库连接成功");
            response.put("version", version);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "数据库连接失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有表名
     * @return 表名列表
     */
    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getAllTables() {
        try {
            List<String> tables = jdbcExampleService.getAllTables();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tables", tables);
            response.put("count", tables.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取表名失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 执行自定义查询
     * @param request 包含SQL语句的请求体
     * @return 查询结果
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> executeQuery(@RequestBody Map<String, String> request) {
        try {
            String sql = request.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            List<Map<String, Object>> results = jdbcExampleService.executeQuery(sql);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("results", results);
            response.put("count", results.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "执行查询失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 执行更新操作
     * @param request 包含SQL语句和参数的请求体
     * @return 更新结果
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> executeUpdate(@RequestBody Map<String, Object> request) {
        try {
            String sql = (String) request.get("sql");
            Object[] args = null;
            if (request.containsKey("args")) {
                List<Object> argsList = (List<Object>) request.get("args");
                args = argsList.toArray();
            }

            if (sql == null || sql.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "SQL语句不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            int rows = jdbcExampleService.executeUpdate(sql, args);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("affectedRows", rows);
            response.put("message", "更新操作执行成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "执行更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
