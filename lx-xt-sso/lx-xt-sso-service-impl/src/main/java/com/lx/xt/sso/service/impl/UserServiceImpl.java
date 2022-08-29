package com.lx.xt.sso.service.impl;

import com.lx.xt.common.model.CallResult;
import com.lx.xt.common.service.AbstractTemplateAction;
import com.lx.xt.sso.domain.UserDomain;
import com.lx.xt.sso.domain.repository.UserDomainRepository;
import com.lx.xt.sso.model.params.UserParam;
import com.lx.xt.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends AbstractService implements UserService {

    @Autowired
    private UserDomainRepository userDomainRepository;

    @Override
    public CallResult userInfo() {
        UserDomain userDomain = userDomainRepository.createDomain(new UserParam());
        return this.serviceTemplate.executeQuery(new AbstractTemplateAction<Object>() {
            @Override
            public CallResult<Object> doAction() {
                return userDomain.userInfo();
            }
        });
    }
}
