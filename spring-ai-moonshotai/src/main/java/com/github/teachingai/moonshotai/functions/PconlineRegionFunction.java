package com.github.partmeai.moonshotai.functions;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * IP地址解析
 * http://whois.pconline.com.cn/
 */
@Slf4j
public class PconlineRegionFunction implements Function<PconlineRegionFunction.Request, PconlineRegionFunction.Response> {

    // 请求连接地址
    private static final String GET_COUNTRY_BY_IP_URL = "https://whois.pconline.com.cn";

    private final static RestClient restClient = RestClient.builder().baseUrl(GET_COUNTRY_BY_IP_URL)
            .defaultStatusHandler(RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER).build();

    private final static Consumer<HttpHeaders> headersConsumer = headers -> {
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        headers.add("Connection","close");
    };

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
            .removalListener((RemovalListener<String, Optional<JSONObject>>) (key, value, cause) -> log.info("{} was removed, cause is {}", key, cause))
            // build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            /**
             * IP地址解析：http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=183.128.136.82
             * @param ip
             * @return {"ip":"110.137.48.237","pro":"","proCode":"999999","city":"","cityCode":"0","region":"","regionCode":"0","addr":" 印度尼西亚","regionNames":"","err":"noprovince"}
             */
            .build(ip -> {
                // 3、调用三方接口解析IP信息
                try {
                    HttpEntity<String> response = restClient.get().uri(uriBuilder -> uriBuilder
                                    .path("/ipJson.jsp")
                                    .queryParam("json", Boolean.TRUE.toString())
                                    .queryParam("ip", ip)
                                    .build())
                            .headers(headersConsumer)
                            .retrieve().toEntity(String.class);
                    String bodyString = response.getBody();
                    log.info(" IP : {} >> Location : {} ", ip, bodyString);
                    JSONObject jsonObject = JSONObject.parseObject(bodyString);
                    String addr = jsonObject.getString("addr");
                    if (StringUtils.hasText(addr)) {
                        return Optional.ofNullable(jsonObject);
                    }
                    log.error("IP : {} >> Location Query Error. Body >> {}", bodyString);
                } catch (Exception e) {
                    log.error("IP : {} >> Location Query Error：{}", e.getMessage());
                }
                return Optional.empty();
            });


    public record Request(String ip) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(
            @JsonProperty("ip") String ip,
            @JsonProperty("pro") String pro,
            @JsonProperty("proCode") String proCode,
            @JsonProperty("city") String city,
            @JsonProperty("cityCode") String cityCode,
            @JsonProperty("region") String region,
            @JsonProperty("regionCode") String regionCode,
            @JsonProperty("addr") String addr,
            @JsonProperty("regionNames") String regionNames,
            @JsonProperty("err") String err) {

    }

    @Override
    public Response apply(Request request) {
        Optional<JSONObject> opt = WEATHER_DATA_CACHES.get(request.ip());
        if (opt.isPresent()) {
            JSONObject jsonObject = opt.get();
            return new Response(
                    jsonObject.getString("ip"),
                    jsonObject.getString("pro"),
                    jsonObject.getString("proCode"),
                    jsonObject.getString("city"),
                    jsonObject.getString("cityCode"),
                    jsonObject.getString("region"),
                    jsonObject.getString("regionCode"),
                    jsonObject.getString("addr"),
                    jsonObject.getString("regionNames"),
                    jsonObject.getString("err")
            );
        }
        return new Response(request.ip(), "", "", "", "", "", "", "", "", "");
    }

}
