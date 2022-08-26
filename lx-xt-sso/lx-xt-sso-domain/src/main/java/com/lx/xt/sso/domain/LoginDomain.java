package com.lx.xt.sso.domain;

import com.lx.xt.common.constants.RedisKey;
import com.lx.xt.common.model.BusinessCodeEnum;
import com.lx.xt.common.model.CallResult;
import com.lx.xt.common.utils.JwtUtil;
import com.lx.xt.sso.dao.data.User;
import com.lx.xt.sso.domain.repository.LoginDomainRepository;
import com.lx.xt.sso.model.params.LoginParam;
import com.lx.xt.sso.model.params.LoginType;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.joda.time.DateTime;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 专门处理登录和相关操作
 */
public class LoginDomain {
    
    private LoginDomainRepository loginDomainRepository;
    
    private LoginParam loginParam;

    private String security = "lx@8862";
    
    public LoginDomain(LoginDomainRepository loginDomainRepository, LoginParam loginParam) {
        this.loginDomainRepository = loginDomainRepository;
        this.loginParam = loginParam;
    }

    public CallResult<Object> buildQrConnectUrl() {
        String url = this.loginDomainRepository.buildQrUrl();
        return CallResult.success(url);
    }

    public CallResult<Object> checkWxLoginCallBackBiz() {
        // 检查业务参数，检查state是否是合法的
        // csrf的检测
        String state = loginParam.getState();
        // 去redis检测，key为state的值是否存在，如果不存在，证明不合法
        boolean isVerify = loginDomainRepository.checkState(state);
        //代码逻辑仔细检查
        if (!isVerify) {
            return CallResult.fail(BusinessCodeEnum.CHECK_BIZ_NO_RESULT.getCode(), "参数不合法");
        }
        return CallResult.success();
    }

    public CallResult<Object> wxLoginCallBack() {
        System.out.println("开始执行业务逻辑");
        // 业务逻辑
        String code = loginParam.getCode();
        try {
            // 2.下次登陆时，refresh_toke如果存在，则可以直接获取access_token，不需要用户重新授权
            String refreshToken = loginDomainRepository.redisTemplate.opsForValue().get(RedisKey.REFRESH_TOKEN);
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = null;
            if (refreshToken == null) {
                // 1.通过code获取access_token、refresh_toke，需要将refresh_token保存在redis中，过期时间为28天
                wxMpOAuth2AccessToken = loginDomainRepository.wxMpService.oauth2getAccessToken(code);
                refreshToken = wxMpOAuth2AccessToken.getRefreshToken();
                loginDomainRepository.redisTemplate.opsForValue().set(RedisKey.REFRESH_TOKEN, refreshToken, 28, TimeUnit.DAYS);
            } else {
                wxMpOAuth2AccessToken = loginDomainRepository.wxMpService.oauth2refreshAccessToken(refreshToken);
            }
            // 3.通过access_token获取微信的用户信息（openid：web端登录的微信id，unionId：微信方全局唯一的用户标识）
            WxMpUser wxMpUser = loginDomainRepository.wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, "zh_CN");
            String unionId = wxMpUser.getUnionId();
            // 4.需要判断unionId在user表中是否存在，存在就更新最后登录时间，不存在就注册
            User user = loginDomainRepository.findUserByUnionId(unionId);
            if (user == null) {
                // 注册
                user = new User();
                Long currentTime = System.currentTimeMillis();
                user.setNickname(wxMpUser.getNickname());
                user.setHeadImageUrl(wxMpUser.getHeadImgUrl());
                user.setSex(wxMpUser.getSex());
                user.setOpenid(wxMpUser.getOpenId());
                user.setLoginType(LoginType.WX.getCode());
                user.setCountry(wxMpUser.getCountry());
                user.setCity(wxMpUser.getCity());
                user.setProvince(wxMpUser.getProvince());
                user.setRegisterTime(currentTime);
                user.setLastLoginTime(currentTime);
                user.setUnionId(unionId);
                user.setArea("");
                user.setMobile("");
                user.setGrade("");
                user.setName(wxMpUser.getNickname());
                user.setSchool("");
                loginDomainRepository.saveUser(user);
            }
            // 5.完成登录操作，通过jwt生成token，
            String token = JwtUtil.createJWT(7 * 24 * 60 * 60 * 1000, user.getId(), security);
            loginDomainRepository.redisTemplate.opsForValue().set(RedisKey.TOKEN + token, String.valueOf(user.getId()), 7, TimeUnit.DAYS);

            System.out.println("redis: " + RedisKey.TOKEN + " ---- " + String.valueOf(user.getId()));
            System.out.println();

            // 6.因为我们属于付费课程，所以账号只能在一端登录，如果用户在其它地方登录，需要将当前登录的用户踢下线
            String oldToken = loginDomainRepository.redisTemplate.opsForValue().get(RedisKey.LOGIN_USER_TOKEN + user.getId());
            if (oldToken != null) {
                // 当前用户之前在某一个设备登录过
                // 当前用户进行登录验证的时候，需要先验证token是否合法，然后去redis查询是否存在token，不存在则代表不合法
                loginDomainRepository.redisTemplate.delete(RedisKey.TOKEN + token);
            }
            loginDomainRepository.redisTemplate.opsForValue().set(RedisKey.LOGIN_USER_TOKEN + user.getId(), token);

            System.out.println("redis: " + RedisKey.LOGIN_USER_TOKEN + user.getId() + " ---- " + token);
            System.out.println();

            // 7.将token返回给前端，存在cookie中，下次请求时，从cookie中获取token（积分，成就，任务...）
            HttpServletResponse response = loginParam.getResponse();
            Cookie cookie = new Cookie("t_token", token);
            cookie.setMaxAge(7 * 24 * 3600);
            cookie.setPath("/");
            response.addCookie(cookie);

            System.out.println("jwt_token: " + token);
            System.out.println();

            // 8.需要记录日志，记录当前用户的登录行为
            return CallResult.success();
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return CallResult.fail(BusinessCodeEnum.LOGIN_WX_NOT_USER_INFO.getCode(), "授权问题，无法获取用户信息");
    }
}
