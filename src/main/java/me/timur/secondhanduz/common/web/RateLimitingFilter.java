package me.timur.secondhanduz.common.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple in-memory token-bucket rate limiting per client IP.
 * For production, replace with Redis-backed implementation.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    @Value("${app.rate-limit.capacity:100}")
    private long capacity;

    @Value("${app.rate-limit.refill-period-seconds:60}")
    private long refillPeriodSeconds;

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String ip = resolveClientIp(request);
        TokenBucket bucket = buckets.computeIfAbsent(ip,
                k -> new TokenBucket(capacity, refillPeriodSeconds));

        if (bucket.tryConsume()) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP: {}", ip);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"status\":429,\"message\":\"Too many requests\"}");
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class TokenBucket {
        private final long capacity;
        private final long refillPeriodMs;
        private final AtomicLong tokens;
        private volatile long lastRefill;

        TokenBucket(long capacity, long refillPeriodSeconds) {
            this.capacity = capacity;
            this.refillPeriodMs = refillPeriodSeconds * 1000L;
            this.tokens = new AtomicLong(capacity);
            this.lastRefill = Instant.now().toEpochMilli();
        }

        synchronized boolean tryConsume() {
            long now = Instant.now().toEpochMilli();
            if (now - lastRefill >= refillPeriodMs) {
                tokens.set(capacity);
                lastRefill = now;
            }
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            return false;
        }
    }
}
