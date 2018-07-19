package com.neusoft.apserver.service;
import com.neusoft.apserver.common.domain.Pagination;

import java.util.Map;

/**
 * 根据ic查询案卷相关信息模块：service层接口
 * @name fandp
 * @email fandp@neusoft.com
 */
public interface IpcSearchService {
    //查询案卷列表
    public Map<String,Object> searchPatentList(String ipc, Pagination pagination);

    //查询中英文解释
    public Map<String,String> searchIpc(String ipc);

}
