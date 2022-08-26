package com.lx.xt.sso.service;

import com.lx.xt.common.model.CallResult;
import com.lx.xt.sso.model.params.LoginParam;

public interface LoginService {
    /**
     * 获取微信扫码的二维码链接地址
     * @return
     */
    CallResult getQRCodeUrl();

    /**
     * 当用户扫码授权之后，进行的登录回调操作
     * @param loginParam
     * @return
     */
    CallResult wxLoginCallBack(LoginParam loginParam);
}
