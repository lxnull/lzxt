package com.lx.xt.sso.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lx.xt.common.constants.RedisKey;
import com.lx.xt.common.wx.config.WxOpenConfig;
import com.lx.xt.sso.dao.UserMapper;
import com.lx.xt.sso.dao.data.User;
import com.lx.xt.sso.domain.LoginDomain;
import com.lx.xt.sso.model.params.LoginParam;
import me.chanjar.weixin.mp.api.WxMpService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class LoginDomainRepository {
    @Resource
    private UserMapper userMapper;
    @Autowired
    public StringRedisTemplate redisTemplate;
    @Autowired
    public WxMpService wxMpService;
    @Autowired
    private WxOpenConfig wxOpenConfig;

    public LoginDomain createDomain(LoginParam loginParam) {
        return new LoginDomain(this, loginParam);
    }

    public boolean checkState(String state) {
        Boolean isValid = redisTemplate.hasKey(RedisKey.WX_STATE_KEY + state);
        return isValid != null && isValid;
    }

    public String buildQrUrl() {
        // String csrfKey = wxOpenConfig.getCsrfKey();
        // String time = new DateTime().toString("yyyyMMddHHmmss");
        // 防止跨站请求伪造，用csrfKey做验证，验证成功表示请求是本人发出的
        // csrf攻击：攻击者盗用了你的身份，以你的身份发出请求，对服务器来说这个请求是完全合法的，但却完成了攻击者期望的操作
        // 将csrfKey放入到redis中，并设置有效期
        // csrfKey = time + "_" + csrfKey;
        String csrfKey = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(RedisKey.WX_STATE_KEY + csrfKey, "1", 60, TimeUnit.SECONDS);
        // 使用第三方工具生成二维码
        String url = wxMpService.buildQrConnectUrl(wxOpenConfig.getRedirectUrl(), wxOpenConfig.getScope(), csrfKey);
        return url;
    }

    public User findUserByUnionId(String unionId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // limit 1:只查一条，查到结果就不往下检索了
        queryWrapper.eq(User::getUnionId, unionId).last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    public void saveUser(User user) {
    }
}
