package com.neusoft.mpserver.jszkoffline.service;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.domain.wordmark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface PatentSearchService {
    //查询案卷列表
    public Map<String,Object> searchPatentList(Pagination pagination) throws IOException, SQLException;
    //查询申请专利和对比文献的详细信息
    public Map<String,Object> searchPatentDetailInfo(String an, String citedAn);
    //查询申请专利和对比文献的详细信息
    public Map<String,Object> searchPatentDetailInfoNoCite(String an);
    //查询标引词
    public List<ZKPatentMark> showMarkList(String an);
    //保存标引词
    public boolean addMark(String userId,String an, List markList,int patenttype);

    //删除错误的分词关键词
    public boolean removeErrorKeyword(String errorKeyword);

    //查询一段文本拆词后词几频率
    public List<ZKPatentMark> searchSortByKeywordFreqsList(String  text);

    //保存特征检索式到数据库
    public boolean addSearchWords(String an,String citedAn,String searchWords,String categoryType,String userId,String searchWords2);
    //从数据库中查询特征检索式
    public wordmark searchWords(String an, String citedAn);
}
