package com.github.hiwepy.huaweiai.pangu.functions;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

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
