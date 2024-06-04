package com.rate.limit.aspects;

import com.rate.limit.annotations.RateLimiter;
import com.rate.limit.exceptions.RateLimitException;
import com.rate.limit.rate_limit.model.request.RateLimitRequest;
import com.rate.limit.rate_limit.service.RateLimitService;
import com.rate.limit.user_token.service.UserTokenExecutor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
@RequiredArgsConstructor
public class RateLimitAspect {

    private final UserTokenExecutor tokenExecutor;
    private final RateLimitService rateLimitService;

    @Before(value = "@annotation(rateLimiter)")
    public void isValid(JoinPoint joinPoint, RateLimiter rateLimiter) {
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
