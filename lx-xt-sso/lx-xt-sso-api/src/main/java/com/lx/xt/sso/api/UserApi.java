package com.lx.xt.sso.api;

import com.lx.xt.common.model.CallResult;
import com.lx.xt.sso.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserApi {

    // /user/userInfo

    @Autowired
    private UserService userService;

    @PostMapping("userInfo")
    public CallResult userInfo() {
        return userService.userInfo();
    }
}
