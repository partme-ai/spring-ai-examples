package com.github.hiwepy.vertexai.functions;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
/**
 * 免费天气查询
 * https://www.sojson.com/api/weather.html
 */
@Slf4j
public class GetWeatherFunction implements Function<GetWeatherFunction.Request, GetWeatherFunction.Response> {

    //请求连接地址
    private final static String SOJSON_WEATHER_URL = "http://t.weather.sojson.com/api/weather/city";

    private final static RestClient restClient = RestClient.builder().baseUrl(SOJSON_WEATHER_URL)
            .defaultStatusHandler(RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER).build();

    private final LoadingCache<String, Optional<JSONObject>> WEATHER_DATA_CACHES = Caffeine.newBuilder()
            // 设置写缓存后1个小时过期
            .expireAfterWrite(1, TimeUnit.HOURS)
            // 设置缓存容器的初始容量为10
            .initialCapacity(10)
            // 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(100)
            // 设置要统计缓存的命中率
            .recordStats()
            // 设置缓存的移除通知
            .removalListener(new RemovalListener<String, Optional<JSONObject>>() {

                @Override
                public void onRemoval(String key, Optional<JSONObject> value, RemovalCause cause) {
                    log.info("{} was removed, cause is {}", key, cause);
                }

            })
            // build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            .build(new CacheLoader<String, Optional<JSONObject>>() {

                @Override
                public Optional<JSONObject> load(String city_code) throws Exception {
                    HttpEntity<String> response = restClient.get().uri(String.format("/api/weather/city/%s", city_code))
                            .retrieve().toEntity(String.class);
                    String bodyString = response.getBody();
                    log.info("city_code {} >> weather :  {}", city_code, bodyString);
                    JSONObject jsonObject = JSONObject.parseObject(bodyString);
                    return Optional.ofNullable(jsonObject);
                }
            });


    public enum Unit { C, F }

    @JsonClassDescription("Get the weather in location")
    public record Request(String cityId, Unit unit) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(
            @JsonProperty("shidu") double shidu,
            @JsonProperty("pm25") double pm25,
            @JsonProperty("pm10") double pm10,
            @JsonProperty("quality") String quality,
            @JsonProperty("wendu") double wendu,
            @JsonProperty("unit") Unit unit) {
        public Response(double wendu, Unit unit) {
            this(0.0, 0.0, 0.0, "", wendu, unit);
        }

    }

    @Override
    public Response apply(Request request) {
        Optional<JSONObject> opt = WEATHER_DATA_CACHES.get(request.cityId());
        if (opt.isPresent()) {
            JSONObject jsonObject = opt.get();
            JSONObject data = jsonObject.getJSONObject("data");
            return new Response(data.getDouble("wendu"), data.getDouble("pm25"), data.getDouble("pm10"),
                    data.getString("quality"), data.getDouble("wendu"), Unit.C);
        }
        return new Response(30.0, Unit.C);
    }

}
