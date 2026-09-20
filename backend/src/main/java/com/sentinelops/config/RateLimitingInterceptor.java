package com.sentinelops.config;

import com.sentinelops.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);

    private final StringRedisTemplate redisTemplate;

    @Value("${rate-limiting.enabled:true}")
    private boolean enabled;

    @Value("${rate-limiting.requests-per-minute:100}")
    private int requestsPerMinute;

    public RateLimitingInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!enabled || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String ip = getClientIp(request);
        long currentMinute = System.currentTimeMillis() / 60000;
        String key = "rate_limit:" + ip + ":" + currentMinute;

        try {
            Long count = redisTemplate.opsForValue().increment(key, 1);
            if (count != null && count == 1) {
                redisTemplate.expire(key, Duration.ofMinutes(1));
            }

            if (count != null && count > requestsPerMinute) {
                logger.warn("Rate limit exceeded for IP {}: {} requests in 1 minute", ip, count);
                throw new RateLimitExceededException("API Rate limit exceeded. Maximum " + requestsPerMinute + " requests per minute allowed.");
            }
        } catch (RateLimitExceededException ex) {
            throw ex;
        } catch (Exception e) {
            logger.debug("Redis rate limiter bypass (Redis unreachable or error): {}", e.getMessage());
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
