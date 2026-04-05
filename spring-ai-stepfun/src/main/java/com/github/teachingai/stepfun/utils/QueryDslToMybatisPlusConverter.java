package com.github.partmeai.stepfun.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;

import java.util.function.Function;

/**
 * QueryDSL Predicate 转换为 MyBatis-Plus Lambda 表达式的工具类
 */
public class QueryDslToMybatisPlusConverter {

    /**
     * 将 QueryDSL Predicate 转换为 MyBatis-Plus LambdaQueryWrapper
     *
     * @param predicate QueryDSL 查询条件
     * @param entityClass 实体类类型
     * @param <T> 实体类泛型
     * @return MyBatis-Plus 查询包装器
     */
    public static <T> LambdaQueryWrapper<T> convert(Predicate predicate, Class<T> entityClass) {
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        if (predicate == null) {
            return queryWrapper;
        }

        // 处理基本表达式
        if (predicate instanceof SimpleExpression) {
            handleSimpleExpression((SimpleExpression<?>) predicate, queryWrapper);
        }
        // 处理字符串表达式
        else if (predicate instanceof StringExpression) {
            handleStringExpression((StringExpression) predicate, queryWrapper);
        }
        // 处理布尔表达式
        else if (predicate instanceof BooleanExpression) {
            handleBooleanExpression((BooleanExpression) predicate, queryWrapper);
        }

        return queryWrapper;
    }

    /**
     * 处理简单表达式
     */
    private static <T> void handleSimpleExpression(SimpleExpression<?> expression, LambdaQueryWrapper<T> queryWrapper) {
        String fieldName = expression.toString().split(" ")[0];
        Object value = ((SimpleExpression<?>) expression).getValue();

        switch (expression.toString().split(" ")[1]) {
            case "=":
                queryWrapper.eq(fieldName, value);
                break;
            case "!=":
                queryWrapper.ne(fieldName, value);
                break;
            case ">":
                queryWrapper.gt(fieldName, value);
                break;
            case "<":
                queryWrapper.lt(fieldName, value);
                break;
            case ">=":
                queryWrapper.ge(fieldName, value);
                break;
            case "<=":
                queryWrapper.le(fieldName, value);
                break;
            default:
                break;
        }
    }

    /**
     * 处理字符串表达式
     */
    private static <T> void handleStringExpression(StringExpression expression, LambdaQueryWrapper<T> queryWrapper) {
        String fieldName = expression.toString().split(" ")[0];
        String value = expression.toString().split(" ")[2];

        if (expression.toString().contains("like")) {
            queryWrapper.like(fieldName, value);
        } else if (expression.toString().contains("startsWith")) {
            queryWrapper.likeRight(fieldName, value);
        } else if (expression.toString().contains("endsWith")) {
            queryWrapper.likeLeft(fieldName, value);
        } else if (expression.toString().contains("contains")) {
            queryWrapper.like(fieldName, value);
        }
    }

    /**
     * 处理布尔表达式
     */
    private static <T> void handleBooleanExpression(BooleanExpression expression, LambdaQueryWrapper<T> queryWrapper) {
        if (expression.toString().contains("and")) {
            String[] conditions = expression.toString().split(" and ");
            for (String condition : conditions) {
                if (condition.contains("=")) {
                    String fieldName = condition.split("=")[0].trim();
                    String value = condition.split("=")[1].trim();
                    queryWrapper.eq(fieldName, value);
                }
            }
        } else if (expression.toString().contains("or")) {
            queryWrapper.or();
            String[] conditions = expression.toString().split(" or ");
            for (String condition : conditions) {
                if (condition.contains("=")) {
                    String fieldName = condition.split("=")[0].trim();
                    String value = condition.split("=")[1].trim();
                    queryWrapper.eq(fieldName, value);
                }
            }
        } else if (expression.toString().contains("not")) {
            String condition = expression.toString().replace("not ", "");
            if (condition.contains("=")) {
                String fieldName = condition.split("=")[0].trim();
                String value = condition.split("=")[1].trim();
                queryWrapper.ne(fieldName, value);
            }
        }
    }

    /**
     * 获取字段名称
     */
    private static <T> String getFieldName(Function<T, ?> function) {
        try {
            String methodName = function.getClass().getDeclaredMethod("apply", Object.class).getName();
            return methodName.substring(methodName.lastIndexOf("$") + 1);
        } catch (Exception e) {
            throw new RuntimeException("无法获取字段名称", e);
        }
    }
}