// src/main/java/com/yourcompany/smallbusinessinvoices/security/RateLimitService.java
package com.sazimtandabuzo.smallbusinessinvoices.security;

import com.sazimtandabuzo.smallbusinessinvoices.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
//import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final int BUCKET_CAPACITY = 100;
    private final int REFILL_AMOUNT = 100;
    private final int REFILL_DURATION = 1; // minutes

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, this::newBucket);
    }

    private Bucket newBucket(String key) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(BUCKET_CAPACITY,
                        Refill.intervally(REFILL_AMOUNT, Duration.ofMinutes(REFILL_DURATION))))
                .build();
    }

    public void checkRateLimit(String key) {
        Bucket bucket = resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again in a few minutes.");
        }
    }
}