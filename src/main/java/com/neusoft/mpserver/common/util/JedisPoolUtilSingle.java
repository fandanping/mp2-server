package com.neusoft.mpserver.common.util;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class JedisPoolUtilSingle {
    //私有构造方法
    private JedisPoolUtilSingle(){}

    public static JedisPool pool = null;

    //静态代码块
    static{
        //读取资源文件
        ResourceBundle bundle = ResourceBundle.getBundle("JedisPool");
        //读取相应的值
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(Integer.parseInt(bundle.getString("minIdle")));//最小空闲连接数
        config.setMaxIdle(Integer.parseInt(bundle.getString("maxIdle")));//最大空闲连接数
        config.setMaxTotal(Integer.parseInt(bundle.getString("maxTotal")));//最大连接数
        config.setMaxWaitMillis(Integer.parseInt(bundle.getString("maxWaitMillis")));//最大等待超时时间
         pool = new JedisPool(config, bundle.getString("host"), Integer.parseInt(bundle.getString("port")));

    }

    //获取连接
    public static Jedis getJedis(){
        return  pool.getResource();
    }

    //关闭连接
    public static void closeJedis(Jedis jedis) {
        try {
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
