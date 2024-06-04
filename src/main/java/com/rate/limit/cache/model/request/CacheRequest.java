package com.rate.limit.cache.model.request;

import com.rate.limit.cache.model.enums.CacheType;

import java.time.Duration;

public record CacheRequest(CacheType type,
                           String key,
                           String value,
                           Duration expirationTime) {
}
