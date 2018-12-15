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
import com.neusoft.mpserver.jszkoffline.dao.CitedInfoRepository;
import com.neusoft.mpserver.jszkoffline.dao.PatentRepository;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.service.PatentSearchService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import thk.analyzer.ThkAnalyzer;
import thk.analyzer.Token;
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

    /**
     *1.查询案卷列表：数据库查询
     * @param pagination 分页对象
     * @return 分页对象+案卷列表（本案卷申请号，对比案卷申请号，对比案卷类型，apIpc,cIpc,location）
     */
    @Override
    public Map<String, Object> searchPatentList(Pagination pagination) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Object[]> searchResult = new ArrayList<Object[]>();
        List<Map<String,Object>>  patentList=new ArrayList<Map<String,Object>>();
        //查询total
        int total=patentRepository.findAnCount();
        pagination.setTotal(total);
        //获取分页信息，封装Pageable
        int size=pagination.getSize();
        int pageNumber = pagination.getStart() /size;
        Pageable pageable = new PageRequest(pageNumber, size);
        //查询结果集
        searchResult=patentRepository.findPatentList(pageable);
        //遍历结果集，封装返回案卷列表
        for(int i=0;i<searchResult.size();i++){
            Object[] itemRestlt = searchResult.get(i);
            Map<String,Object>  temporaryMap=new HashMap<String,Object>();
            temporaryMap.put("an",itemRestlt[0].toString().substring(0,searchResult.get(i)[0].toString().indexOf(".")));
            temporaryMap.put("citedAn",itemRestlt[2] == null ?"" :searchResult.get(i)[2].toString());
            temporaryMap.put("citeType",itemRestlt[3] ==null ? "":searchResult.get(i)[3].toString());
            temporaryMap.put("apIpc",itemRestlt[4] == null ?"" :searchResult.get(i)[4].toString());
            temporaryMap.put("cIpc",itemRestlt[5] == null ?"" :searchResult.get(i)[5].toString());
            temporaryMap.put("apoldAn",itemRestlt[0].toString());
            temporaryMap.put("location",itemRestlt[6] == null ?"" :searchResult.get(i)[6].toString());
            patentList.add(temporaryMap);
        }
        resultMap.put("anList",patentList);
        resultMap.put("pagination",pagination);
        return resultMap;
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
        Map<String, String> citeInfoMap= new HashMap<String,String>();
        //1.到CNABS库中查询标题，申请人，发明人，国省代码等
        //1.1 设置trs CNABS 查询条件字段
        Condition conditionCNABS = new Condition();
        String searchAn = "nrd_an=('" + an + "' or '"+citedAn+"')";//nrd_an=( 'CN201510493315' or 'CN01128416')
        conditionCNABS.setExp(searchAn);
        conditionCNABS.setDbName(Constant.CNABS_DB);
        conditionCNABS.setDisplayFields(Constant.GK_PN + "," + Constant.GK_FIELDS + "," + Constant.SQ_FIELDS + "," + Constant.OTHER_FIELDS);
        //1.2 查询
        TrsResult tr = trsEngine.search(conditionCNABS);
        //1.3 获取结果集
        List<Record> recordList = tr.getRecords();
        int size = recordList.size();
        for(int i=0;i<size;i++){
            Map<String, String> assembleData=AssembleData(recordList.get(i).getDataMap());
            if(assembleData.get("NRD_AN").equals(an)){
                patentInfoMap=assembleData;
            }else if(assembleData.get("NRD_AN").equals(citedAn)){
                citeInfoMap=assembleData;
            }
        }
        String patentTi= patentInfoMap.get("TI");
        String patentCitedTi=citeInfoMap.get("TI");
        //2. 查询权利要求及说明书：CNTXT
        //2.1 设置trs CNABS 查询条件字段
        Condition conditionCNTXT = new Condition();
        conditionCNTXT.setExp(searchAn);
        conditionCNTXT.setDbName(Constant.CNTXT_DB);
        conditionCNTXT.setDisplayFields(Constant.CLMS + "," + Constant.DESC + "," + Constant.CNTXT_AN);
        //2.2 查询
        TrsResult tr1 = trsEngine.search(conditionCNTXT);
        //2.3 获取结果集
        List<Record> recordList1 = tr1.getRecords();
        Map<String, String> selfTemporaryMap=new HashMap<String,String>();
        Map<String, String> citeTemporaryMap=new HashMap<String,String>();
        for(int i=0;i<recordList1.size();i++){
            Map<String, String> temp=recordList1.get(i).getDataMap();
            if(temp.get("NRD_AN").equals(an)){
                selfTemporaryMap=temp;
            }else if(temp.get("NRD_AN").equals(citedAn)){
                citeTemporaryMap=temp;
            }
        }
        //2.4 格式化权利要求和说明书（去除多余标签）
        String patentCLMS= XmlFormatter.format(selfTemporaryMap.get("CLMS"), XmlFormatter.XmlType.CLMS);
        String patentDESC=XmlFormatter.format(selfTemporaryMap.get("DESC1"), XmlFormatter.XmlType.DESC);
        String citedCLMS=XmlFormatter.format(citeTemporaryMap.get("CLMS"), XmlFormatter.XmlType.CLMS);
        String citedDESC=XmlFormatter.format(citeTemporaryMap.get("DESC1"), XmlFormatter.XmlType.DESC);
        //2.5 封装到map中
        patentInfoMap.put("CLIMS", patentCLMS);
        patentInfoMap.put("DESC", patentDESC);
        citeInfoMap.put("CLIMS", citedCLMS);
        citeInfoMap.put("DESC", citedDESC);
        //4. 封装返回对象
        resultmMap.put("thispatentBaseInfo", patentInfoMap);
        resultmMap.put("citepatentBaseInfo", citeInfoMap);
        //3. 标题，权利要求，说明书调用拆词接口进行拆词,并将返回的集合排序并封装返回map
        try {
            resultmMap.put("patentChaiCiTi",filterListByTokenName(sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentTi))));
            resultmMap.put("citedChaiCiTi",filterListByTokenName(sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentCitedTi))));
            resultmMap.put("patentCLMSChaiCiTi",filterListByTokenName(sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentCLMS))));
            resultmMap.put("patentDESCChaiCiTi",filterListByTokenName(sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(patentDESC))));
            resultmMap.put("citedCLMSChaiCiTi",filterListByTokenName( sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(citedCLMS))));
            resultmMap.put("citedDESCChaiCiTi",filterListByTokenName(sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(citedDESC))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultmMap;
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
        Collections.sort(sourceList, new TokenComparator());
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
     * 无引文 申请号
     * @param an
     * @return
     */
   @Deprecated
    public Map<String, Object> searchPatentDetailInfoNoCite(String an) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> patentBaseInfoMap= new HashMap<String,String>();
        Condition condition = new Condition();
        Condition condition1 = new Condition();
        String searchAn = "nrd_an=('" + an +"')";//nrd_an=( 'CN201510493315')
        condition.setExp(searchAn);
        condition.setDbName(Constant.CNABS_DB);
        condition.setDisplayFields(Constant.GK_PN + "," + Constant.GK_FIELDS + "," + Constant.SQ_FIELDS + "," + Constant.OTHER_FIELDS);
        TrsResult tr = trsEngine.search(condition);
        List<Record> recordList = tr.getRecords();
        int size = recordList.size();
        for(int i=0;i<size;i++){
            Map<String, String> assembleData=AssembleData(recordList.get(i).getDataMap());
            if(assembleData.get("NRD_AN").equals(an)){
                patentBaseInfoMap=assembleData;
            }
        }
        //查询权利要求及说明书
        condition1.setExp(searchAn);
        condition1.setDbName(Constant.CNTXT_DB);
        condition1.setDisplayFields(Constant.CLMS + "," + Constant.DESC + "," + Constant.CNTXT_AN);
        TrsResult tr1 = trsEngine.search(condition1);
        List<Record> recordList1 = tr1.getRecords();
        Map<String, String> selfMap=new HashMap<String,String>();
        Map<String, String> citeMap=new HashMap<String,String>();
        for(int i=0;i<recordList1.size();i++){
            Map<String, String> temp=recordList1.get(i).getDataMap();
            if(temp.get("NRD_AN").equals(an)){
                selfMap=temp;
            }
        }
        patentBaseInfoMap.put("CLIMS",((selfMap.get("CLMS")).replaceAll("<[^>]+>","")).replace("\n","@#"));
        patentBaseInfoMap.put("DESC",((selfMap.get("DESC1")).replaceAll("<[^>]+>","")).replace("\n","<div></div>").replace("技术领域","<div class='desc-title' style='color:red'>技术领域</div>").replace("背景技术","<div class='desc-title' style='color:red'>背景技术</div>").replace("发明内容","<div class='desc-title' style='color:red'>发明内容</div>").replace("附图说明","<div class='desc-title'>附图说明</div>").replace("具体实施方式","<div class='desc-title' style='color:red'>具体实施方式</div>"));

        map.put("thispatentBaseInfo", patentBaseInfoMap);
        //map.put("citepatentBaseInfo", '');
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
        return resultMap;
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
        JedisCluster jedis = JedisPoolUtil.getJedis();
        //Jedis jedis= JedisPoolUtilSingle.getJedis();
        String mark=jedis.get("zkmark"+an);
        Map markMap=gson.fromJson(mark,Map.class);
        if(markMap!=null){
            String tiMarkWord=markMap.get("zkTiWord").toString();
            String clmsMarkWord=markMap.get("zkClmsWord").toString();
            String descMarkWOrd=markMap.get("zkDescWord").toString();
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
            if(clmsMarkWord.length()!=0){
                String[] clms=clmsMarkWord.split(",");
                for(int i=0;i<clms.length;i++){
                    ZKPatentMark clmsmark=new ZKPatentMark();
                    clmsmark.setAn(zkAn);
                    clmsmark.setType("2");
                    clmsmark.setWord(clms[i]);
                    list.add(clmsmark);
                }
            }
            if(descMarkWOrd.length()!=0){
                String[] clms=descMarkWOrd.split(",");
                for(int i=0;i<clms.length;i++){
                    ZKPatentMark descmark=new ZKPatentMark();
                    descmark.setAn(zkAn);
                    descmark.setType("3");
                    descmark.setWord(clms[i]);
                    list.add(descmark);
                }
            }
        }
        //JedisPoolUtilSingle.closeJedis(jedis);
        return list;
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
    public boolean addMark(String userId, List markList) {
        Gson gson=new Gson();
        List<ZKPatentMark> zkPatentMarkList=markList;
        String an=zkPatentMarkList.get(0).getAn();
        JedisCluster jedis = JedisPoolUtil.getJedis();
        //Jedis jedis= JedisPoolUtilSingle.getJedis();
        Map<String,String> markMap=new HashMap<String,String>();
        String timark ="";
        String clmsmark ="";
        String descmark="";
        for(int i=0;i<zkPatentMarkList.size();i++){
            ZKPatentMark item=zkPatentMarkList.get(i);
            String  type=item.getType();
            if(type.equals("1")){
                timark += item.getWord()+",";
            }else if(type.equals("2")){
                clmsmark += item.getWord()+",";
            }else if(type.equals("3")){
                descmark += item.getWord()+",";
            }
        }
        if(timark.length()!=0){
            String timarkResult=timark.substring(0,timark.length()-1);
            markMap.put("zkTiWord",timarkResult);
        }else{
            markMap.put("zkTiWord","");
        }
        if(clmsmark.length()!=0){
            String clmsmarkResult=clmsmark.substring(0,clmsmark.length()-1);
            markMap.put("zkClmsWord",clmsmarkResult);
        }else{
            markMap.put("zkClmsWord","");
        }
        if(descmark.length()!=0){
            String descmarkResult=descmark.substring(0,descmark.length()-1);
            markMap.put("zkDescWord",descmarkResult);
        }else{
            markMap.put("zkDescWord","");
        }
        markMap.put("zkAn",zkPatentMarkList.get(0).getAn());
        jedis.set("zkmark"+an, gson.toJson(markMap));
       // JedisPoolUtilSingle.closeJedis(jedis);
        return true;
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
            list= sortByTokenFrequence(ThkAnalyzer.getInstance().analysis(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filterListByTokenName(list);
    }

}
