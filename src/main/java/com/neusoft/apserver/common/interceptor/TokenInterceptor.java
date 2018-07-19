package com.neusoft.apserver.common.interceptor;

import com.neusoft.apserver.dao.TokenRepository;
import com.neusoft.apserver.domain.Constant;
import com.neusoft.apserver.domain.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token验证拦截器
 * @name fandp
 * @email fandp@neusoft.om
 */
public class TokenInterceptor implements HandlerInterceptor{
    @Autowired
    private TokenRepository tokenRepository;
    @Value("${system.params.env}")
    private String env;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(env.equals("development")){
            return true;
        }
        String token = request.getParameter("token");
        Token tokenDomain=tokenRepository.findByTokenId(token);
        if (tokenDomain == null) {
            //Map<String,String> map=new HashMap<String, String>();
            //map.put("error",Constant.NO_LOGIN);
            //Gson gson = new Gson();
            //String resultStr = gson.toJson(map);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            request.getRequestDispatcher("/user/nologin").forward(request, response);
            return false;
        } else {
            request.setAttribute(Constant.USER_ID, tokenDomain.getUserId());
            //token没有过期放行
            return true;
        }
    }
}
