package com.lx.xt.sso.service;

public interface TokenService {
    /**
     * token 认证
     * @param token
     * @return
     */
    Long checkToken(String token);
}
