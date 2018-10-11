package com.neusoft.mpserver.jszkoffline.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "SIPO_MP_TI_MARK")
public class ElectrialTiMark {
    //id
    @Id
    private String id;
    //类型：共四种类型，标题,权力要求
    //1:标题
    private String type;
    //用户id
    private String userId;
    //案卷号
    private String an;
    //标引词
    private String word;
    //标引时间
    private String createTime;

}
