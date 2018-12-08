package com.neusoft.mpserver.jszkoffline.service.impl;

import com.google.gson.Gson;
import com.neusoft.mpserver.common.domain.Condition;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.domain.Record;
import com.neusoft.mpserver.common.domain.TrsResult;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.common.util.XmlFormatter;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.service.ZKPatentMarkService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import thk.analyzer.ThkAnalyzer;

import java.util.*;

/**
 * 智库案卷标引：6月份-9月份 排除外观 的业务逻辑处理类
 */
@Service
public class ZKPatentMarkServiceImpl implements ZKPatentMarkService {
    @Autowired
    private TrsEngine trsEngine;
    /**
     * trs查询CNABS库公开日在6月份-九月份 ,排除外观，按照公开日降序，分页查询，返回想要展示的字段信息
     * @param pagination
     * @return
     */
    @Override
    public Map<String, Object> searchZKPatentList(Pagination pagination) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String,String>>  patentList=new ArrayList<Map<String,String>>();
        Condition condition = new Condition();
        //检索式
        String searchExp = "pd >='2018.08' and pd<='2018.09' and inty='fm'";
        condition.setExp(searchExp);
        //选库
        condition.setDbName(Constant.CNABS_DB);
        //显示字段 :申请号 标题 公开日 主分类
        condition.setDisplayFields(Constant.CNTXT_AN + "," + Constant.GK_TI+","+Constant.PD+","+Constant.IPC_MAIN+","+Constant.GK_PN+","+Constant.GK_FIELDS+","+ Constant.SQ_FIELDS + "," + Constant.OTHER_FIELDS);
        //分页查询
        Pagination page=new Pagination();
        page.setStart(pagination.getStart());
        page.setSize(pagination.getSize());
        condition.setPagination(page);
        TrsResult tr = trsEngine.search(condition);
        //获取trs查询结果
        List<Record> recordList = tr.getRecords();
        //结果集记录数
        int resultSize = recordList.size();
        //封装返回结果集
        Map<String, String> patentInfoMap= new HashMap<String,String>();
        for(int i=0;i<resultSize;i++){
            Map<String, String> assembleData=AssembleData(recordList.get(i).getDataMap());
            patentList.add(assembleData);
        }
        map.put("zkPatentListResult",patentList);
        map.put("pagination",pagination);
        return map;
    }
    /**
     * 默认取GK（公开）字段数据，若为空，则取SQ（授权）字段数据
     *
     * @param dataMap 检索出的trs行数据
     * @return 组装后的数据
     */
    private Map<String, String> AssembleData(Map<String, String> dataMap) {
        Map<String, String> resultMap = new HashMap<String, String>();
        String gkPn = dataMap.get(Constant.GK_PN);
        String ap=dataMap.get(Constant.CNTXT_AN).split(" ")[0];
        String PD=dataMap.get(Constant.PD);
        String ipcMain=dataMap.get(Constant.IPC_MAIN);
        String prefix = Constant.GK_PREFIX;
        if (gkPn == null || gkPn.equals("")) {
            prefix = Constant.SQ_PREFIX;
        }
        String[] mainFields = Constant.MAIN_FIELDS.split(",");
        for (String f : mainFields) {
            resultMap.put(f, dataMap.get(prefix + f));
        }
        String[] otherFields = Constant.OTHER_FIELDS.split(",");
        for (String f : otherFields) {
            resultMap.put(f, dataMap.get(f));
        }
        resultMap.put("Ap",ap);
        resultMap.put("PD",PD);
        resultMap.put("ipcMain",ipcMain);
        return resultMap;
    }

    @Override
    public Map<String, Object> searchZKPatentDetailInfo(String an) {
        Map<String,Object> map=new HashMap<String,Object>();
        Condition condition=new Condition();
        //检索式
        String searchExp = "nrd_an=('" + an +"')";
        condition.setExp(searchExp);
        condition.setDbName(Constant.CNTXT_DB);
        condition.setDisplayFields(Constant.CLMS + ","+ Constant.CNTXT_AN+","+Constant.DESC);
        TrsResult result = trsEngine.search(condition);
        List<Record> recordList = result.getRecords();
        //结果集记录数
        Map<String, String> temp=recordList.get(0).getDataMap();
        String clms=XmlFormatter.format(temp.get(Constant.CLMS),XmlFormatter.XmlType.CLMS);
        String nrdan=temp.get(Constant.CNTXT_AN);
        String desc=XmlFormatter.format(temp.get(Constant.DESC),XmlFormatter.XmlType.DESC);
        Map newmap=new HashMap();
        newmap.put("CLIMS",clms);
        newmap.put("nrdan",nrdan);
        newmap.put("DESC",desc);
        List ClmsChaici=new ArrayList();
        try {
            ClmsChaici= ThkAnalyzer.getInstance().analysis(clms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("ClmsChaici",ClmsChaici);
        map.put("ZKPatentDetailInfo",newmap);
        return map;
    }

    /**
     * 查询标题拆词参考
     * @param title
     * @return
     */
    @Override
    public List showChaiCiList(String title) {
        List result=new ArrayList();
        try {
            result= ThkAnalyzer.getInstance().analysis(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
    public boolean addZKMark(String userId, List markList) {
        Gson gson=new Gson();
        List<ZKPatentMark> zkPatentMarkList=markList;
        String an=zkPatentMarkList.get(0).getAn();
        Jedis jedis = new Jedis("localhost",6379);
        Map<String,String> tiMarkMap=new HashMap<String,String>();
        Map<String,String> clmsMarkMap=new HashMap<String,String>();
        String timark ="";
        String clmsmark ="";
        for(int i=0;i<zkPatentMarkList.size();i++){
            ZKPatentMark item=zkPatentMarkList.get(i);
            String  type=item.getType();
           if(type.equals("1")){
               timark = item.getWord()+",";
           }else if(type.equals("2")){
               clmsmark = item.getWord()+",";
           }
        }
        String timarkResult=timark.substring(0,timark.length()-1);
        if(timarkResult !=""){
            tiMarkMap.put("an",zkPatentMarkList.get(0).getAn());
            tiMarkMap.put("type","1");
            tiMarkMap.put("word",timarkResult);
            System.out.println(gson.toJson(tiMarkMap));
            jedis.set(an+"|"+1, gson.toJson(tiMarkMap));
        }
        if(clmsmark !=""){
            String clmsmarkResult=timark.substring(0,clmsmark.length()-1);
            clmsMarkMap.put("an",zkPatentMarkList.get(0).getAn());
            clmsMarkMap.put("type","2");
            tiMarkMap.put("word",clmsmarkResult);
            jedis.set(an+"|"+2, gson.toJson(clmsMarkMap));
        }
        //map中的全部键值
        System.out.println(jedis.get(an+"|"+1) );
        return true;
    }

    /**
     * 查询显示标引词
     * @param an
     * @return
     */
    @Override
    public List<ZKPatentMark> showMarkList(String an) {
        Gson gson=new Gson();
        List<ZKPatentMark> list=new ArrayList<ZKPatentMark>();
        Jedis jedis = new Jedis("localhost",6379);
        String tiMark=jedis.get(an+"|"+1);
        Map tiMarkMap=gson.fromJson(tiMark,Map.class);
        String clmsMark=jedis.get(an+"|"+2);
        Map clmsMarkMap=gson.fromJson(clmsMark,Map.class);
        if(tiMarkMap!=null){
           String[] ti=tiMarkMap.get("word").toString().split(",");
           for(int i=0;i<ti.length;i++){
               ZKPatentMark mark=new ZKPatentMark();
               mark.setAn(tiMarkMap.get("an").toString());
               mark.setType(tiMarkMap.get("type").toString());
               mark.setWord(ti[i]);
               System.out.println(ti[i]);
               list.add(mark);
           }
        }
        if(clmsMarkMap!=null){
            String[] clms=clmsMarkMap.get("word").toString().split(",");
            for(int i=0;i<clms.length;i++){
                ZKPatentMark mark=new ZKPatentMark();
                mark.setAn(clmsMarkMap.get("an").toString());
                mark.setType(clmsMarkMap.get("type").toString());
                mark.setWord(clms[i]);
                list.add(mark);
            }
        }
        return list;
    }

}
