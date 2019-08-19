package com.neusoft.mpserver.jszkoffline.service.impl;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.common.util.XmlFormatter;
import com.neusoft.mpserver.jszkoffline.common.DBAndTrsService;
import com.neusoft.mpserver.jszkoffline.dao.PatentRepository;
import com.neusoft.mpserver.jszkoffline.dao.WordMarkRepository;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.domain.wordmark;
import com.neusoft.mpserver.jszkoffline.service.PatentSearchService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thk.analyzer.ThkAnalyzer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * 查询申请案卷及对比文献的基本信息：数据库中查询（uni_abs_patcit_cn_0905 jszk_0826）
 */
@Service
public class PatentSearchServiceImpl implements PatentSearchService {
    @Autowired
    private TrsEngine trsEngine;
    @Autowired
    private PatentRepository patentRepository;
    @Autowired
    private DBAndTrsService dbAndTrsService;
    @Autowired
    private WordMarkRepository wordMarkRepository;
    /**
     *1.查询案卷列表：数据库查询：UNI_ABS_PATCIT_CN_NEW
     * @param pagination 分页对象
     * @return 分页对象+案卷列表（本案卷申请号，对比案卷申请号，对比案卷类型,location）
     */
    @Override
    public Map<String, Object> searchPatentList(Pagination pagination) throws IOException, SQLException {
       return   dbAndTrsService.searchPatentList(pagination);
    }

    /**
     * 查询申请专利和对比文献专利的详细信息：查询trs （标题 发明人 申请人 国省代码 权利要求 说明书 摘要）
     * @param an
     * @param citedAn
     * @return
     */
    @Override
    public Map<String, Object> searchPatentDetailInfo(String an, String citedAn) {
        Map<String, Object> resultmMap = new HashMap<String, Object>();
        Map<String, String> patentInfoMap= new HashMap<String,String>();
        Map<String, String> citedInfoMap= new HashMap<String,String>();
        //1.到CNABS库中查询标题，申请人，发明人，国省代码等
        Map<String,Map<String,String>> temp= dbAndTrsService.searchTrsToCNABS(an,citedAn);
        patentInfoMap = temp.get("patentInfoMap");
        citedInfoMap = temp.get("citedInfoMap");
        String patentTi= patentInfoMap.get("TI");
        String patentCitedTi=citedInfoMap.get("TI");
        //2. 查询权利要求及说明书：CNTXT
        Map<String,Map<String,String>> detailTemp = dbAndTrsService.searchTrsToCNTXT(an,citedAn);
        Map<String,String> selfTemporaryMap = detailTemp.get("selfTemporaryMap");
        Map<String,String> citeTemporaryMap = detailTemp.get("citeTemporaryMap");
        //2.4 格式化权利要求和说明书（去除多余标签）
        String patentCLMS= XmlFormatter.format(selfTemporaryMap.get("CLMS"), XmlFormatter.XmlType.CLMS);
        String patentDESC=XmlFormatter.format(selfTemporaryMap.get("DESC1"), XmlFormatter.XmlType.DESC);
        String citedCLMS=XmlFormatter.format(citeTemporaryMap.get("CLMS"), XmlFormatter.XmlType.CLMS);
        String citedDESC=XmlFormatter.format(citeTemporaryMap.get("DESC1"), XmlFormatter.XmlType.DESC);
        //2.5 封装到map中
        patentInfoMap.put("CLIMS", patentCLMS);
        patentInfoMap.put("DESC", patentDESC);
        citedInfoMap.put("CLIMS", citedCLMS);
        citedInfoMap.put("DESC", citedDESC);
        //4. 封装返回对象
        resultmMap.put("thispatentBaseInfo", patentInfoMap);
        resultmMap.put("citepatentBaseInfo", citedInfoMap);
        //3. 标题，权利要求，说明书调用拆词接口进行拆词,并将返回的集合排序并封装返回map
        try {
            resultmMap.put("patentChaiCiTi",dbAndTrsService.filterListByTokenName(dbAndTrsService.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentTi))));
            resultmMap.put("citedChaiCiTi",dbAndTrsService.filterListByTokenName(dbAndTrsService.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentCitedTi))));
            resultmMap.put("patentCLMSChaiCiTi",dbAndTrsService.filterListByTokenName(dbAndTrsService.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentCLMS))));
            resultmMap.put("patentDESCChaiCiTi",dbAndTrsService.filterListByTokenName(dbAndTrsService.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentDESC))));
            resultmMap.put("citedCLMSChaiCiTi",dbAndTrsService.filterListByTokenName( dbAndTrsService.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(citedCLMS))));
            resultmMap.put("citedDESCChaiCiTi",dbAndTrsService.filterListByTokenName(dbAndTrsService.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(citedDESC))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultmMap;
    }
    /**
     * 无引文 申请号
     * @param an
     * @return
     */

    public Map<String, Object> searchPatentDetailInfoNoCite(String an) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> patentBaseInfoMap= new HashMap<String,String>();
        Map<String, String> patentClamAnfDesc= new HashMap<String,String>();
        patentBaseInfoMap = dbAndTrsService.searchTrsToCNABS(an);
        patentClamAnfDesc = dbAndTrsService.searchTrsToCNTXT(an);
        patentBaseInfoMap.put("CLIMS",XmlFormatter.format(patentClamAnfDesc.get(Constant.CLMS),XmlFormatter.XmlType.CLMS));
        patentBaseInfoMap.put("DESC",XmlFormatter.format(patentClamAnfDesc.get(Constant.DESC),XmlFormatter.XmlType.DESC));
        map.put("thispatentBaseInfo", patentBaseInfoMap);
        return map;
    }

    /**
     * 查询显示标引词
     * 到redis查询
     * @param an
     * @return
     */
    @Override
    public List<ZKPatentMark> showMarkList(String an) {
         return dbAndTrsService.searchMarkFromRedis(an);
    }
    /**
     * 保存标引词
     * 保存到redis
     * @param an
     * @param
     * @param markList
     * @return
     */
    @Override
    public boolean addMark(String userId,String an, List markList,int patenttype) {
         return dbAndTrsService.saveMarkToRedis(userId,an,markList,patenttype);
    }

    /**
     * 保存错误的分词
     * @param errorKeyword
     * @return
     */
    @Override
    public boolean removeErrorKeyword(String errorKeyword) {
        return dbAndTrsService.removeErrorKeyword(errorKeyword);
    }

    /**
     * 传入一段文本，调用拆词接口，返回拆词及频率
     * @param text
     * @return
     */
    @Override
    public List<ZKPatentMark> searchSortByKeywordFreqsList(String text) {
        List list=new ArrayList();
        try {
            list= dbAndTrsService.sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbAndTrsService.filterListByTokenName(list);
    }
    //保存特征检索式到数据库中
    @Override
    public boolean addSearchWords(String an, String citedAn, String searchWords,String categoryType,String userId,String searchWords2) {
        int flag;
        if(wordMarkRepository.findSearchWords(an,citedAn) != null){
            flag=wordMarkRepository.updateSearchWords2(an,citedAn,searchWords,categoryType,userId,searchWords2);
        }else {
            flag=wordMarkRepository.saveSearchWords2(an,citedAn,searchWords,categoryType,userId,searchWords2);
        }
        return (flag>0) ? true : false;
    }
    //从数据库中查询特征检索式
    @Override
    public wordmark searchWords(String an, String citedAn) {
        return wordMarkRepository.findSearchWords(an,citedAn);
    }


}
