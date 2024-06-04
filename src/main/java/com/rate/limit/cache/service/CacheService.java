package com.rate.limit.cache.service;

import com.rate.limit.cache.model.enums.CacheType;

import java.time.Duration;

public interface CacheService {

    void setWithExpiration(String key, String value, Duration expirationTime);

    String getRecord(String key);

    boolean canHandle(CacheType cacheType);
}
