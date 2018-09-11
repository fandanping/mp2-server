package com.neusoft.mpserver.sipo57.controller;

import com.neusoft.mpserver.sipo57.domain.Constant;
import com.neusoft.mpserver.sipo57.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 地址标引-用户模块
 * @name fandp
 * @email fandp@neusoft.com
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    /**
     * 当token失效时转发到该路由
     * @return
     */
    @GetMapping("/nologin")
    public Map<String, String> noLogin(){
        Map<String, String> result = new HashMap<String, String>();
        result.put("error", Constant.NO_LOGIN);
        return result;
    }
    /**
     * 注册功能
     * @param postMap
     * @return
     */
    @PostMapping("/add")
    public Map<String, String> addUser(@RequestBody Map postMap) {
        String username = (String) postMap.get("username");
        String password = (String) postMap.get("password");
        return userService.addUser(username, password);
    }
    /**
     * 登录
     * @param
     * @return
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map postMap) {
        String username = (String) postMap.get("username");
        String password = (String) postMap.get("password");
        Map<String, Object> map = new HashMap<String, Object>();
        map = userService.login(username, password);
        return map;
    }

    /**
     * 退出
     *
     * @param
     * @return
     */
    @PostMapping("/logout")
    public Map<String,Object> logout(HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constant.USER_ID);
        Map<String,Object> map=new HashMap<String,Object>();
        boolean flag=userService.logout(userId);
        map.put("flag",flag);
        return  map;
    }


}
