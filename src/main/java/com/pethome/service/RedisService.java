package com.pethome.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethome.common.exception.BizException;
import com.pethome.common.result.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务：统一封装字符串读写 + 对象 JSON 序列化读写
 */
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /** 存字符串 */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /** 存对象（JSON 序列化） */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), timeout, unit);
        } catch (JsonProcessingException e) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "缓存序列化失败");
        }
    }

    /** 取字符串 */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 取对象（JSON 反序列化） */
    public <T> T get(String key, TypeReference<T> typeRef) {
        String json = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /** 删除 key */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
