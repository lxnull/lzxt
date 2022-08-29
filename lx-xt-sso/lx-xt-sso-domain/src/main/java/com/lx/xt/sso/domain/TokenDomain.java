package com.lx.xt.sso.domain;

import com.lx.xt.common.constants.RedisKey;
import com.lx.xt.common.utils.JwtUtil;
import com.lx.xt.sso.domain.repository.TokenDomainRespository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class TokenDomain {

    private TokenDomainRespository tokenDomainRespository;

    public TokenDomain(TokenDomainRespository tokenDomainRespository) {
        this.tokenDomainRespository = tokenDomainRespository;
    }

    public Long checkToken(String token) {
        // 1.检查token字符串是否合法
        // 2.检查redis是否有此token
        try {
            JwtUtil.parseJWT(token, LoginDomain.security);
            String userIdStr = tokenDomainRespository.redisTemplate.opsForValue().get(RedisKey.TOKEN + token);
            if (StringUtils.isBlank(userIdStr)) {
                return null;
            }
            return Long.parseLong(userIdStr);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkToken error:{}", e.getMessage(), e);
            return null;
        }
    }
}
