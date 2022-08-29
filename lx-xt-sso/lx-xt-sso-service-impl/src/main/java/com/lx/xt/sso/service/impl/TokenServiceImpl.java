package com.lx.xt.sso.service.impl;

import com.lx.xt.common.model.CallResult;
import com.lx.xt.common.service.AbstractTemplateAction;
import com.lx.xt.sso.domain.TokenDomain;
import com.lx.xt.sso.domain.repository.TokenDomainRespository;
import com.lx.xt.sso.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl extends AbstractService implements TokenService {

    @Autowired
    private TokenDomainRespository tokenDomainRespository;

    @Override
    public Long checkToken(String token) {
        TokenDomain tokenDomain = tokenDomainRespository.createDomain(null);
        return tokenDomain.checkToken(token);
    }
}
