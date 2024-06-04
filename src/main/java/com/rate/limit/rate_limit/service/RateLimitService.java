package com.rate.limit.rate_limit.service;

import com.rate.limit.cache.model.enums.CacheType;
import com.rate.limit.cache.model.request.CacheRequest;
import com.rate.limit.cache.service.CacheExecutor;
import com.rate.limit.rate_limit.model.request.RateLimitRequest;
import com.rate.limit.rate_limit.model.response.RateLimitModel;
import com.rate.limit.utils.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
public class RateLimitService {

    private final CacheExecutor cacheExecutor;

    public boolean isAllowed(RateLimitRequest request) {
        String cacheKey = getCacheKey(request.key(), request.userToken());
        String rateLimitRecord = cacheExecutor.getRecord(request.cacheType(), cacheKey);

        RateLimitModel model = StringUtils.isBlank(rateLimitRecord) ?
                createModelAndCacheRecord(request.key(), request.duration(), request.cacheType()) :
                getModelAndUpdateCachedRecord(request.key(), request.duration(), rateLimitRecord, request.cacheType());

        return model.getRequestCount() <= request.maximumNumberOfRequests();
    }

    private RateLimitModel getModelAndUpdateCachedRecord(String key,
                                                         long duration,
                                                         String rateLimitRecord,
                                                         CacheType type) {
        RateLimitModel model = MapperUtil
                .convertFromStringToObject(rateLimitRecord, RateLimitModel.class)
                .orElseGet(() -> createModelAndCacheRecord(key, duration, type));

        model.setRequestCount(model.getRequestCount() + 1);
        Optional<String> value = MapperUtil.convertObjectToString(model);

        value.ifPresent(val -> {
            Duration remainingDuration = Duration.ofMinutes(model.getExpiration());
            CacheRequest request = new CacheRequest(type, key, val, remainingDuration);
            cacheExecutor.setWithExpiration(request);
        });

        return model;
    }

    private RateLimitModel createModelAndCacheRecord(String key,
                                                     long duration,
                                                     CacheType type) {
        RateLimitModel model = new RateLimitModel(1, duration);
        Optional<String> value = MapperUtil.convertObjectToString(model);
        Duration expiration = Duration.ofMinutes(duration);

        value.ifPresent(val -> {
            CacheRequest request = new CacheRequest(type, key, val, expiration);
            cacheExecutor.setWithExpiration(request);
        });
        return model;
    }

    private static String getCacheKey(String key, String token) {
        return String.format("%s - %s", key, token);
    }
}
