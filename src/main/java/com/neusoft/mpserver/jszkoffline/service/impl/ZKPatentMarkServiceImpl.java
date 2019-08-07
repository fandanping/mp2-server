package com.neusoft.mpserver.jszkoffline.service.impl;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.domain.Record;
import com.neusoft.mpserver.common.domain.TrsResult;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.common.util.XmlFormatter;
import com.neusoft.mpserver.jszkoffline.common.DBAndTrsService;
import com.neusoft.mpserver.jszkoffline.dao.ZKPatentRepository;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.service.ZKPatentMarkService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thk.analyzer.ThkAnalyzer;
import java.util.*;

/**
 * 智库案卷标引：6月份-9月份 排除外观 的业务逻辑处理类
 */
@Service
public class ZKPatentMarkServiceImpl implements ZKPatentMarkService {
    @Autowired
    private  TrsEngine trsEngine;
    @Autowired
    private ZKPatentRepository zkPatentRepository;
    @Autowired
    private DBAndTrsService dbAndTrsService;
    /**
     * trs查询CNABS库公开日在6月份-九月份 ,排除外观，按照公开日降序，分页查询，返回想要展示的字段信息（标题 摘要 公开日 ）
     * @param pagination
     * @return
     */
    @Override
    public Map<String, Object> searchZKPatentList(Pagination pagination) {
    //1. 初始化
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String,String>>  patentList=new ArrayList<Map<String,String>>();
    //2. 拼接检索条件
        String searchExp = "pd >='2018.06.1' and pd<='2018.07.1' and ti= '%曲轴%'";
        //String searchExp = "pd >='2018.11.15' and pd<='2018.12.31' and inty='fm' and ic='A61K36'";
        //pd >='2018.12.13' and pd<='2018.12.18' and inty='fm'
        //pd >='2018.11.15' and pd<='2018.12.31' and inty='fm' and ic='A61K36'
       // String searchExp = "pd >='2018.11.1' and pd<='2018.11.5' and inty='fm' and ic='A61K36'";
        //String searchExp = "pd >='2018.06' and pd<='2018.09' and inty='fm' and ti='%电缆%' ";
        //gk_pd >='2018.11.1' and gk_pd<='2018.11.5' and inty='fm' and ic='A61K36'
        //String searchExp = "pd >='2018.11.1' and pd<='2018.12.31' and pa='珠海格力电器股份有限公司'";
        String fields =Constant.CNTXT_AN + "," + Constant.GK_TI+","+Constant.PD+","+Constant.IPC_MAIN+","+Constant.GK_PN+","+Constant.GK_FIELDS+","+ Constant.SQ_FIELDS + "," + Constant.OTHER_FIELDS;
        String dbName = Constant.CNABS_DB;
        Pagination page=new Pagination();
        page.setStart(pagination.getStart());
        page.setSize(pagination.getSize());
    //3. 调用接口查询结果集
        TrsResult tr = dbAndTrsService.searchTRS(dbName,searchExp,fields, page );
    //4. 获取trs查询结果
        List<Record> recordList = tr.getRecords();
        //4.1 结果集记录数
        int resultSize = recordList.size();
    //5. 封装返回结果集
        for(int i=0;i<resultSize;i++){
            Map<String, String> assembleData=dbAndTrsService.AssembleData(recordList.get(i).getDataMap());
            patentList.add(assembleData);
        }
        pagination.setTotal(tr.getPagination().getTotal());
        map.put("zkPatentListResult",patentList);
        map.put("pagination",pagination);
        return map;
    }

    /**
     * 从数据库 表中查询列表 ，目前该方法没有实现 ，用不上
     * @param pagination
     * @return
     */
    @Override
    public Map<String, Object> searchZKPatentListFromOracle(Pagination pagination) {
        Map<String, Object> map = new HashMap<String, Object>();
        return map;
    }

    /**
     * trs查询权利要求，说明书：传入案卷号an,到CNTXT库查询权力要求，调用拆词接口，查询权利要求拆词参考
     * @param an
     * @return
     */
    @Override
    public Map<String, Object> searchZKPatentDetailInfo(String an) {
    // 1. 初始化
        Map<String,Object> map=new HashMap<String,Object>();
    // 2. 拼接检索条件
        String searchExp = "nrd_an=('" + an +"')";
        String dbName = Constant.CNTXT_DB;
        String displayFields = Constant.CLMS + ","+ Constant.CNTXT_AN+","+Constant.DESC;
    // 3. 获取结果集
        TrsResult result = dbAndTrsService.searchTRS(dbName,searchExp,displayFields);
        List<Record> recordList = result.getRecords();
        //结果集记录数
    // 4. 结果集处理
        if(recordList.size() ==0){
            map.put("ClmsChaici","");
            map.put("DescChaici","");
            map.put("ZKPatentDetailInfo","");
        }else{
            Map<String, String> temp=recordList.get(0).getDataMap();
            String clms=XmlFormatter.format(temp.get(Constant.CLMS),XmlFormatter.XmlType.CLMS);
            String nrdan=temp.get(Constant.CNTXT_AN);
            String desc=XmlFormatter.format(temp.get(Constant.DESC),XmlFormatter.XmlType.DESC);
            Map newmap=new HashMap();
            newmap.put("CLIMS",clms);
            newmap.put("nrdan",nrdan);
            newmap.put("DESC",desc);
            List ClmsChaici=new ArrayList();
            List DescChaici=new ArrayList();
            try {
                ClmsChaici= ThkAnalyzer.getInstance().analysis(clms);
                DescChaici=ThkAnalyzer.getInstance().analysis(desc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.put("ClmsChaici",dbAndTrsService.filterListByTokenName(dbAndTrsService.sortByTokenFrequence(ClmsChaici)));
            map.put("DescChaici",dbAndTrsService.filterListByTokenName(dbAndTrsService.sortByTokenFrequence(DescChaici)));
            map.put("ZKPatentDetailInfo",newmap);
        }
        return map;
    }

    /**
     * 查询标题拆词参考
     * @param title
     * @return
     */
    @Override
    public List showChaiCiList(String title) {
        return dbAndTrsService.getSplitwordList(title);
    }

    /**
     * 保存标引词
     * 保存到redis
     * @param userId
     * @param
     * @param markList
     * @return
     */
    @Override
    public boolean addZKMarkToRedis(String userId,String an, List markList,int patenttype) {
         return dbAndTrsService.saveMarkToRedis(userId,an,markList,patenttype);
    }

    /**
     * 保存标引词 到数据库
     * @param userId
     * @param markList
     * @return
     */
    @Override
    public boolean addZKMark(String userId, String an,List markList,int patenttype) {
         return dbAndTrsService.saveMarkToOracle(userId,an,markList,patenttype);
    }

    /**
     * 查询显示标引词 从oracle
     * @param an
     * @return
     */
    @Override
    public List<ZKPatentMark> showMarkList(String an) {
        return dbAndTrsService.searchMarkFromOracle(an);
    }

    /**
     * 查询显示标引词  redis
     * 到redis查询
     * @param an
     * @return
     */
   @Override
    public List<ZKPatentMark> showMarkListFromRedis(String an) {
       return dbAndTrsService.searchMarkFromRedis(an);
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

}
