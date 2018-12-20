package com.neusoft.mpserver.jszkoffline.service.impl;

import com.google.gson.Gson;
import com.neusoft.mpserver.common.domain.Condition;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.domain.Record;
import com.neusoft.mpserver.common.domain.TrsResult;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.common.util.JedisPoolUtil;
import com.neusoft.mpserver.common.util.JedisPoolUtilSingle;
import com.neusoft.mpserver.common.util.XmlFormatter;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.service.ZKPatentMarkService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import thk.analyzer.ThkAnalyzer;
import thk.analyzer.Token;

import java.util.*;

/**
 * 智库案卷标引：6月份-9月份 排除外观 的业务逻辑处理类
 */
@Service
public class ZKPatentMarkServiceImpl implements ZKPatentMarkService {
    @Autowired
    private  TrsEngine trsEngine;
    /**
     * trs查询CNABS库公开日在6月份-九月份 ,排除外观，按照公开日降序，分页查询，返回想要展示的字段信息（标题 摘要 公开日 ）
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
        //排序:降序
        condition.setSortFields("-"+"pd");
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
        //查询结果集总数
        //System.out.println("计数"+ tr.getPagination().getTotal());
        //封装返回结果集
        Map<String, String> patentInfoMap= new HashMap<String,String>();
        for(int i=0;i<resultSize;i++){
            Map<String, String> assembleData=AssembleData(recordList.get(i).getDataMap());
            patentList.add(assembleData);
        }
        pagination.setTotal(tr.getPagination().getTotal());
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

    /**
     * trs查询权利要求，说明书：传入案卷号an,到CNTXT库查询权力要求，调用拆词接口，查询权利要求拆词参考
     * @param an
     * @return
     */
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
        List DescChaici=new ArrayList();
        try {
            ClmsChaici= ThkAnalyzer.getInstance().analysis(clms);
            DescChaici=ThkAnalyzer.getInstance().analysis(desc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("ClmsChaici",filterListByTokenName(sortByTokenFrequence(ClmsChaici)));
        map.put("DescChaici",filterListByTokenName(sortByTokenFrequence(DescChaici)));
        map.put("ZKPatentDetailInfo",newmap);
        return map;
    }
    private List filterListByTokenName(List source){
        List target=new ArrayList();
        int size=source.size();
        if(source == null || size < 0){
            return  null;
        }
        for(int i=0;i<size; i++){
            Token token= (Token) source.get(i);
            int length = token.getWord().length();
            if(length >= 2){
                target.add(token);
            }
        }
        return target;
    }
    private List sortByTokenFrequence(List sourceList){
        Collections.sort(sourceList, new ZKPatentMarkServiceImpl.TokenComparator());
        return sourceList;
    }
    private class TokenComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Token token1 = (Token) o1;
            Token token2 = (Token) o2;
            if (token1.getFreq() > token2.getFreq())
                return -1;
            if(token1.getFreq() < token2.getFreq())
                return 1;
            return 0;
        }
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
        //Jedis jedis= JedisPoolUtilSingle.getJedis();
        JedisCluster jedis = JedisPoolUtil.getJedis();
        Map<String,String> markMap=new HashMap<String,String>();
        String timark ="";
        String othersmark ="";
        for(int i=0;i<zkPatentMarkList.size();i++){
            ZKPatentMark item=zkPatentMarkList.get(i);
            String  type=item.getType();
           if(type.equals("1")){
               timark += item.getWord()+",";
           }else if(type.equals("2")){
               othersmark += item.getWord()+",";
           }
        }
        if(timark.length()!=0){
            String timarkResult=timark.substring(0,timark.length()-1);
            markMap.put("zkTiWord",timarkResult);
        }else{
            markMap.put("zkTiWord","");
        }
        if(othersmark.length()!=0){
            String clmsmarkResult=othersmark.substring(0,othersmark.length()-1);
            markMap.put("zkOthersWord",clmsmarkResult);
        }else{
            markMap.put("zkOthersWord","");
        }
        markMap.put("zkAn",zkPatentMarkList.get(0).getAn());
        jedis.set("zk"+an, gson.toJson(markMap));
        System.out.println(gson.toJson(markMap));

        //JedisPoolUtil.closeJedis(jedis);
        return true;
    }

    /**
     * 查询显示标引词
     * 到redis查询
     * @param an
     * @return
     */
    @Override
    public List<ZKPatentMark> showMarkList(String an) {
        Gson gson=new Gson();
        List<ZKPatentMark> list=new ArrayList<ZKPatentMark>();
        //Jedis jedis= JedisPoolUtilSingle.getJedis();
        JedisCluster jedis = JedisPoolUtil.getJedis();
        String mark=jedis.get("zk"+an);
        Map markMap=gson.fromJson(mark,Map.class);
        if(markMap!=null){
            String tiMarkWord=markMap.get("zkTiWord").toString();
            String othersMarkWord=markMap.get("zkOthersWord").toString();
            String zkAn=markMap.get("zkAn").toString();
            if(tiMarkWord.length()!=0){
                String[] ti=tiMarkWord.split(",");
                for(int i=0;i<ti.length;i++){
                    ZKPatentMark timark=new ZKPatentMark();
                    timark.setAn(zkAn);
                    timark.setType("1");
                    timark.setWord(ti[i]);
                    list.add(timark);
                }
            }
            if(othersMarkWord.length()!=0){
                String[] othersWord=othersMarkWord.split(",");
                for(int i=0;i<othersWord.length;i++){
                    ZKPatentMark clmsmark=new ZKPatentMark();
                    clmsmark.setAn(zkAn);
                    clmsmark.setType("2");
                    clmsmark.setWord(othersWord[i]);
                    list.add(clmsmark);
                }
            }
        }
        //JedisPoolUtil.closeJedis(jedis);
        return list;
    }

}
