package com.neusoft.mpserver.jszkoffline.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class ZKPatentMark {
    //类型：共2种类型，标题,权利要求
    //1:标题 2：权利要求
    private String type;
    //用户id
    private String userId;
    //案卷号
    @Id
    private String an;
    //标引词
    private String word;
}
