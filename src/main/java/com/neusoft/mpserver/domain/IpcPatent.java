package com.neusoft.mpserver.domain;
import lombok.Data;

@Data
public class IpcPatent {
    //an 案卷号
    private String an;
    //ti 标题
    private String ti;
    //in 发明人
    private String in;
    //paas 申请人
    private String pa;
    //国省名称
    private String cname;
    //国省代码
    private String ccode;

}
