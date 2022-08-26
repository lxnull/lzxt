package com.lx.xt.common.wx.config;

import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class WxOpenConfig {

    @Value("${wx.open.config.appid}")
    private String loginAppid;
    @Value("${wx.open.config.secret}")
    private String loginSecret;
    @Value("${wx.open.config.csrfKey}")
    public String csrfKey;
    @Value("${wx.open.config.redirectUrl}")
    public String redirectUrl;
    @Value("${wx.open.config.scope}")
    public String scope;

    @Bean
    public WxMpService wxMpService() {
        WxMpService service = new WxMpServiceImpl();
        WxMpDefaultConfigImpl wxMpConfigStorage = new WxMpDefaultConfigImpl();
        // 微信登录的账号
        wxMpConfigStorage.setAppId(loginAppid);
        // 密码
        wxMpConfigStorage.setSecret(loginSecret);
        service.setWxMpConfigStorage(wxMpConfigStorage);
        return service;
    }

}
