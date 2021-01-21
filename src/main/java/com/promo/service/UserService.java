package com.promo.service;

import com.promo.error.BusinessException;
import com.promo.service.model.UserModel;

public interface UserService {

    // 通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);

    // 用户注册服务
    void register(UserModel userModel) throws BusinessException;

    // 验证码校验服务
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;
}
