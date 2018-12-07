package com.neusoft.mpserver.jszkoffline.service;

import com.neusoft.mpserver.common.domain.Pagination;

import java.util.Map;

public interface PatentSearchService {
    //查询案卷列表
    public Map<String,Object> searchPatentList(Pagination pagination);
    //查询申请专利和对比文献的详细信息
    public Map<String,Object> searchPatentDetailInfo(String an, String citedAn);
    //查询申请专利和对比文献的详细信息
    public Map<String,Object> searchPatentDetailInfoNoCite(String an);
}
