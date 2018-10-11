package com.neusoft.mpserver.jszkoffline.service;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.domain.ElectrialTiMark;
import com.neusoft.mpserver.sipo57.domain.IpcMark;

import java.util.List;
import java.util.Map;

public interface ElectricalService {
    //查询案卷列表
    public Map<String,Object> searchPatentList(Pagination pagination);
    //查询申请专利的详细信息
    public Map<String,Object> searchPatentDetailInfo(String an);
    //查询标引词
    public List<ElectrialTiMark> showMarkList(String an);
    //保存标引词
    public boolean addMark(String userId, List<ElectrialTiMark> markList);
    //删除标引词
    public boolean deleteMark(List<ElectrialTiMark> list);
    //查询标题拆词
    public List showChaiCiList(String title);
}
