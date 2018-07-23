package com.neusoft.mpserver.service;

import java.util.Map;
/**
 * 用户模块service层接口
 * @name fandp
 * @email fandp@neusoft.com
 */
public interface UserService {
    //用户注册
    public Map<String, String> addUser(String username, String password);
    //用户登录
    public Map<String ,Object> login (String username,String password);
    //用户退出
    public  boolean logout(String userId);
}
