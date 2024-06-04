package com.rate.limit.aspects;

import com.rate.limit.annotations.RateLimiter;
import com.rate.limit.exceptions.RateLimitException;
import com.rate.limit.rate_limit.model.request.RateLimitRequest;
import com.rate.limit.rate_limit.service.RateLimitService;
import com.rate.limit.user_token.service.UserTokenExecutor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateLimitAspect {

    private final UserTokenExecutor tokenExecutor;
    private final RateLimitService rateLimitService;

    public void isValid(RateLimiter rateLimiter) {
        String token = tokenExecutor.getToken(rateLimiter.tokenType());
        RateLimitRequest request = new RateLimitRequest(
                rateLimiter.resource(),
                rateLimiter.maximumNumberOfRequests(),
                rateLimiter.durationInMinutes(),
                rateLimiter.cacheType(),
                token
        );

        if (!rateLimitService.isAllowed(request)) {
            throw new RateLimitException(rateLimiter.durationInMinutes());
        }
    }

}
