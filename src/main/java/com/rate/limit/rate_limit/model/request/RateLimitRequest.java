package com.rate.limit.rate_limit.model.request;

import com.rate.limit.cache.model.enums.CacheType;

public record RateLimitRequest(String key,
                               int maximumNumberOfRequests,
                               long duration,
                               CacheType cacheType,
                               String userToken) {
}
