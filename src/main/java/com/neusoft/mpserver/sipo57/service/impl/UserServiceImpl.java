package com.neusoft.mpserver.sipo57.service.impl;

import com.neusoft.mpserver.common.util.IDGenerator;
import com.neusoft.mpserver.sipo57.dao.TokenRepository;
import com.neusoft.mpserver.sipo57.dao.UserRepository;
import com.neusoft.mpserver.sipo57.domain.Constant;
import com.neusoft.mpserver.sipo57.domain.Token;
import com.neusoft.mpserver.sipo57.domain.User;
import com.neusoft.mpserver.sipo57.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户模块service层实现
 * @name fandp
 * @email fandp@neusoft.com
 */
@Service
public class UserServiceImpl  implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    /**
     * 注册逻辑
     * @param username
     * @param password
     * @return
     */
    @Transactional
    @Override
    public Map<String, String> addUser(String username, String password) {
        Map<String, String> map =new HashMap<String, String>();
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            String userId = IDGenerator.generate();
            user.setId(userId);
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            user.setCreateTime(df.format(day));
            userRepository.save(user);
            map.put("code", Constant.SUCCESS_REGISTER);
        } else {
            map.put("code", Constant.FAIL_REGISTER);
        }
        return map;
    }
    /**
     * 登录逻辑
     *
     * @param username
     * @param password
     * @return
     */
    @Transactional
    @Override
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (userRepository.findByUsername(username).isEmpty()) {
            map.put("code", Constant.NOUSER_LOGIN);
            return map;
        } else if (userRepository.findByUsernameAndPassword(username, password) == null) {
            map.put("code", Constant.FAILPASS_LOGIN);
            return map;
        } else {
            //登录成功
            map.put("code", Constant.SUCCESS_LOGIN);
            User user = userRepository.findByUsernameAndPassword(username, password);
            User loginUser = new User();
            loginUser.setCreateTime(user.getCreateTime());
            loginUser.setId(user.getId());
            loginUser.setUsername(user.getUsername());
            map.put("user", loginUser);
            //token
            tokenRepository.deleteByUserId(user.getId());
            Token token = new Token();
            String tokenId = IDGenerator.generate();
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastTime = df.format(day);
            token.setTokenId(tokenId);
            token.setLastTime(lastTime);
            token.setUserId(user.getId());
            map.put("token", tokenRepository.save(token).getTokenId());
            return map;
        }
    }
    /**
     * 退出逻辑
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public boolean logout(String userId) {
        tokenRepository.deleteByUserId(userId);
        return true;
    }

}
