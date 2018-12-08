package com.neusoft.mpserver.sipo57.domain;

/**
 * 常量值
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
public class Constant {
    //注册成功code码值
    public static final String SUCCESS_REGISTER = "1";
    //注册失败code码值
    public static final String FAIL_REGISTER = "2";
    //登录查询没有用户码值
    public static final String NOUSER_LOGIN = "2";
    //登录成功
    public static final String SUCCESS_LOGIN = "1";
    //登录查询密码错误
    public static final String FAILPASS_LOGIN = "3";
    //当前还未登录
    public static final String NO_LOGIN = "1";
    //登录过期代码
    public static final String EXPIRED_LOGIN = "2";

    //根据ic查询中文英文解释码值
    public static final String IPC_FIELDS = "IC,UTCN,UTEN";

    public static final String CNABS_DB = "CNABS";
    public static final String CNTXT_DB = "CNTXT";
    public static final String CNTXT_AN = "NRD_AN";
    /*********** zhengchj ***********/
    public static final String CLMS="CLMS";
    public static final String DESC="DESC1";
    public static final String AB="AB";
    public static final String GK_PN = "GK_PN";
    public static final String GK_FIELDS = "GK_PA,GK_IN,GK_TI,GK_AB";
    public static final String SQ_FIELDS = "SQ_PA,SQ_IN,SQ_TI,SQ_AB";
    public static final String GK_PREFIX = "GK_";
    public static final String SQ_PREFIX = "SQ_";
    public static final String MAIN_FIELDS = "PA,IN,TI,AB";
    public static final String OTHER_FIELDS = "NRD_AN,CCODE,CNAME";
    public static final String IPC_DB = "IPC8";
    /************ fandp ****************/
    public static final String USER_ID = "user_id";
    public static final String NO_IPC = "0";
    public static final String GK_TI="GK_TI";
    public static final String PD="PD";
    public static final String IPC_MAIN="IPC_MAIN";
}