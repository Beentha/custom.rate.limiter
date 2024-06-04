package com.rate.limit.cache.service;

import com.rate.limit.cache.model.enums.CacheType;
import com.rate.limit.cache.model.request.CacheRequest;
import com.rate.limit.exceptions.NotImplementedException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CacheExecutor {

    private final List<CacheService> cacheServices;

    public void setWithExpiration(CacheRequest request) {
        CacheService service = getCacheService(request.type());

        service.setWithExpiration(request.key(), request.value(), request.expirationTime());
    }

    public String getRecord(CacheType cacheType, String key) {
        CacheService service = getCacheService(cacheType);

        return service.getRecord(key);
    }

    private CacheService getCacheService(CacheType cacheType) {
        return cacheServices
                .stream()
                .filter(cacheService -> cacheService.canHandle(cacheType))
                .findFirst()
                .orElseThrow(() -> new NotImplementedException(String.format("No implementation for %s cache service", cacheType)));
    }
}
