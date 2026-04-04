package com.github.teachingai.amazon.bedrock.functions;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * 工具函数注册：使用 Spring AI 1.1.x {@link FunctionToolCallback} API（替代已移除的 FunctionCallback）。
 */
@Configuration
public class FunctionConfig {

    @Bean
    @Description("获取天气：根据给出的城市ID获取天气信息")
    public Function<GetWeatherFunction.Request, GetWeatherFunction.Response> weatherFunction() {
        return new GetWeatherFunction();
    }

    @Bean
    public ToolCallback ipRegionFunctionInfo() {
        return FunctionToolCallback.builder("getLocationByIp", new PconlineRegionFunction())
                .description("IP地址解析: 根据IP解析IP所在位置信息")
                .inputType(PconlineRegionFunction.Request.class)
                .inputSchema(
                        """
                                {
                                  "type": "object",
                                  "properties": {
                                    "ip": {
                                      "type": "string"
                                    }
                                  },
                                  "required": ["ip"]
                                }""")
                .toolCallResultConverter((response, _type) -> String.format("您的IP当前位置是：%s",
                        ((PconlineRegionFunction.Response) response).addr()))
                .build();
    }
}
