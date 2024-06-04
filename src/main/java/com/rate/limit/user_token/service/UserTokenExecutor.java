package com.rate.limit.user_token.service;

import com.rate.limit.exceptions.NotImplementedException;
import com.rate.limit.user_token.model.enums.UserTokenType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserTokenExecutor {

    private final List<UserTokenService> userTokenServices;

    public String getToken(UserTokenType type) {
        UserTokenService service = getUserTokenService(type);

        return service.getToken();
    }

    private UserTokenService getUserTokenService(UserTokenType type) {
        return userTokenServices
                .stream()
                .filter(service -> service.canHandle(type))
                .findFirst()
                .orElseThrow(() -> new NotImplementedException(String.format("No implementation for %s user token service", type)));
    }
}
