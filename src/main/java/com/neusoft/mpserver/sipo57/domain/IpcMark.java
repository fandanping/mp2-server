package com.neusoft.mpserver.sipo57.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * mark映射实体类
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@Data
@Entity
@Table(name = "SIPO_MP_IPC_MARK")
public class IpcMark {
    //id
    @Id
    private String id;
    //类型：共四种类型，标题,发明人，申请人，国省代码
    //1:标题 2：发明人 3：申请人 4：国省代码
    private String type;
    //用户id
    private String userId;
    //案卷号
    private String an;
    //ipc
    private String ipc;
    //标引词
    private String word;
    //标引时间
    private String createTime;


}
