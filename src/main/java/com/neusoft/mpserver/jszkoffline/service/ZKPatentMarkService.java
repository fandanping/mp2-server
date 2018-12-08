package com.neusoft.mpserver.jszkoffline.service;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.sipo57.domain.IpcMark;

import java.util.List;
import java.util.Map;

/**
 * 智库案卷标引：6月份-9月份 排除外观 的业务接口类
 */
public interface ZKPatentMarkService {
    //查询案卷列表（CNABS ：申请号 标题等）
    public Map<String,Object> searchZKPatentList(Pagination pagination);
    //查询申请专利的详细信息（CNTXT: 权力要求）
    public Map<String,Object> searchZKPatentDetailInfo(String an);
    //查询标题拆词
    public List showChaiCiList(String title);
    //保存标引词
    public boolean addZKMark(String userId, List markList);
    //查询标引词
    public List<ZKPatentMark> showMarkList(String an);

}
