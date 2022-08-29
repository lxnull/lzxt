package com.lx.xt.sso.service;

import com.lx.xt.common.model.CallResult;

public interface UserService {
    /**
     * 获取用户登录信息
     * @return
     */
    CallResult userInfo();
}
