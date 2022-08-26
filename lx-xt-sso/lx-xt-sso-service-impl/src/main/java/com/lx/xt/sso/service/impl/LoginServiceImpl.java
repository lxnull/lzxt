package com.lx.xt.sso.service.impl;

import com.lx.xt.common.constants.RedisKey;
import com.lx.xt.common.model.CallResult;
import com.lx.xt.common.service.AbstractTemplateAction;
import com.lx.xt.common.wx.config.WxOpenConfig;
import com.lx.xt.sso.domain.LoginDomain;
import com.lx.xt.sso.domain.repository.LoginDomainRepository;
import com.lx.xt.sso.model.params.LoginParam;
import com.lx.xt.sso.service.LoginService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl extends AbstractService implements LoginService {

    @Autowired
    private LoginDomainRepository loginDomainRepository;

    @Override
    public CallResult getQRCodeUrl() {
        LoginDomain loginDomain = loginDomainRepository.createDomain(new LoginParam());
        return this.serviceTemplate.executeQuery(new AbstractTemplateAction<Object>() {
            @Override
            public CallResult<Object> doAction() {
                return loginDomain.buildQrConnectUrl();
            }
        });
    }

    @Override
    @Transactional
    public CallResult wxLoginCallBack(LoginParam loginParam) {
        System.out.println("进入 Impl 的 wxLoginCallBack");
        LoginDomain loginDomain = loginDomainRepository.createDomain(loginParam);
        // 带有事务的执行操作
        return this.serviceTemplate.execute(new AbstractTemplateAction<Object>() {

            @Override
            public CallResult<Object> checkBiz() {
                //检查业务参数
                System.out.println("检查业务参数");
                return loginDomain.checkWxLoginCallBackBiz();
            }

            @Override
            public CallResult<Object> doAction() {
                //写业务逻辑
                System.out.println("执行业务逻辑");
                return loginDomain.wxLoginCallBack();
            }
        });
    }
}
