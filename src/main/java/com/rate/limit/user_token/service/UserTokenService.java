package com.rate.limit.user_token.service;

import com.rate.limit.user_token.model.enums.UserTokenType;

public interface UserTokenService {

    String getToken();

    boolean canHandle(UserTokenType type);
}
