package com.lx.xt.sso.api;

import com.lx.xt.common.model.CallResult;
import com.lx.xt.sso.model.params.LoginParam;
import com.lx.xt.sso.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @Controller注解默认是跳转页面的，微信登录是需要有对应的跳转页面的
@Controller
@RequestMapping("login")
public class LoginApi {

    @Autowired
    private LoginService loginService;

    @PostMapping("getQRCodeUrl")
    @ResponseBody
    public CallResult getQRCodeUrl() {
        // 接受参数，返回处理接口
        return loginService.getQRCodeUrl();
    }

    // http://www.lzxtedu.com/api/sso/login/wxLoginCallBack?code=041UKK000RBFqO1BNG20040CCq1UKK0p&state=20220825170322_mszlu_xt
    // redirect_uri?code=CODE&state=STATE
    @GetMapping("wxLoginCallBack")
    public String wxLoginCallBack(HttpServletRequest req, HttpServletResponse resp, String code, String state) {
        System.out.println("进入 wxLoginCallBack 方法");
        // 为了service层统一，所有的api层的参数处理，都放入LoginParam中
        LoginParam loginParam = new LoginParam();
        loginParam.setCode(code);
        loginParam.setState(state);
        loginParam.setRequest(req);
        // 后续 登陆成功之后，要生成token，提供给前端，把token放入对应的cookie中
        loginParam.setResponse(resp);
        CallResult callResult = loginService.wxLoginCallBack(loginParam);
        if (callResult.isSuccess()) {
            // 课程列表
            return "redirect:http://www.lzxtedu.com/course";
        } else {
            return "redirect:http://www.lzxtedu.com/";
        }
    }
}
