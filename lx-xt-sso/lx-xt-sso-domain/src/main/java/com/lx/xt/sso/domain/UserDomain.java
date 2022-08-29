package com.lx.xt.sso.domain;

import com.lx.xt.common.login.UserThreadLocal;
import com.lx.xt.common.model.CallResult;
import com.lx.xt.sso.dao.data.User;
import com.lx.xt.sso.domain.repository.UserDomainRepository;
import com.lx.xt.sso.model.UserModel;
import com.lx.xt.sso.model.params.UserParam;
import org.springframework.beans.BeanUtils;

/**
 * 用户领域，权责明确，用户的事情应该交给用户的domain来实现
 */
public class UserDomain {

    private UserDomainRepository userDomainRepository;

    private UserParam userParam;

    public UserDomain(UserDomainRepository userDomainRepository, UserParam userParam) {
        this.userDomainRepository = userDomainRepository;
        this.userParam = userParam;
    }

    public void updateUser(User user) {
        userDomainRepository.updateUser(user);
    }

    public void saveUser(User user) {
        userDomainRepository.saveUser(user);
    }

    public User findUserByUnionId(String unionId) {
        return userDomainRepository.findUserByUnionId(unionId);
    }

    public CallResult<Object> userInfo() {
        Long userId = UserThreadLocal.get();
        User user = userDomainRepository.findUserById(userId);
        // 注意：返回的信息不能是user，user只是数据库表字段的映射实体，和业务中使用的User是不一样的
        // view层需要有自己的实体对象，来去映射页面的显示
        UserModel userModel = new UserModel();
        BeanUtils. copyProperties(user, userModel);
        return CallResult.success(userModel);
    }
}
