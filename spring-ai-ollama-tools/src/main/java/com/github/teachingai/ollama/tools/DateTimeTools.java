package com.github.teachingai.ollama.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 声明式工具示例：供 Spring AI 根据 {@link Tool} 元数据注册为可调用的工具方法。
 */
@Component
public class DateTimeTools {

    /**
     * 返回当前时区下的日期时间字符串。
     */
    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    /**
     * 演示用闹钟设置（控制台输出），生产环境应接入真实调度系统。
     *
     * @param time ISO-8601 格式时间字符串
     */
    @Tool(description = "Set a user alarm for the given time, provided in ISO-8601 format")
    void setAlarm(String time) {
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("Alarm set for " + alarmTime);
    }
}
