package com.lx.xt.sso.domain.repository;

import com.lx.xt.sso.domain.TokenDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TokenDomainRespository {

    @Autowired
    public StringRedisTemplate redisTemplate;

    public TokenDomain createDomain(Object o) {
        return new TokenDomain(this);
    }
}
