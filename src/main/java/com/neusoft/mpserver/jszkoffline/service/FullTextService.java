package com.neusoft.mpserver.jszkoffline.service;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.domain.FullText;

import java.util.Map;

public interface FullTextService {
    //查询案卷列表
    public Map<String,Object> searchFullTextList(Pagination pagination);

    //查询申请专利的详细信息
    public Map<String,Object> searchFullTextInfo(String an);
}

