package com.lx.xt.sso.domain.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lx.xt.sso.dao.UserMapper;
import com.lx.xt.sso.dao.data.User;
import com.lx.xt.sso.domain.UserDomain;
import com.lx.xt.sso.model.params.UserParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserDomainRepository {

    @Resource
    private UserMapper userMapper;

    public UserDomain createDomain(UserParam userParam) {
        return new UserDomain(this, userParam);
    }

    public User findUserByUnionId(String unionId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // limit 1:只查一条，查到结果就不往下检索了
        queryWrapper.eq(User::getUnionId, unionId).last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    public void saveUser(User user) {
        userMapper.insert(user);
    }

    public void updateUser(User user) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getLastLoginTime, user.getLastLoginTime());
        updateWrapper.eq(User::getId, user.getId());
        userMapper.update(null, updateWrapper);
    }

    public User findUserById(Long userId) {
        return userMapper.selectById(userId);
    }
}
