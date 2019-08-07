package com.neusoft.mpserver.jszkoffline.common;

import com.google.gson.Gson;
import com.neusoft.mpserver.common.domain.Condition;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.domain.Record;
import com.neusoft.mpserver.common.domain.TrsResult;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.common.util.JedisPoolUtil;
import com.neusoft.mpserver.jszkoffline.dao.PatentRepository;
import com.neusoft.mpserver.jszkoffline.dao.ZKPatentRepository;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;
import thk.analyzer.ThkAnalyzer;
import thk.analyzer.Token;
import java.util.*;

@Service
public class DBAndTrsService {
    @Autowired
    private TrsEngine trsEngine;
    @Autowired
    private PatentRepository patentRepository;
    @Autowired
    private ZKPatentRepository zkPatentRepository;

    /**
     * 查询数据库表中案卷信息
     * 传参：分页
     *
     * @param pagination
     * @return
     */
    public Map<String, Object> searchPatentList(Pagination pagination) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Object[]> searchResult = new ArrayList<Object[]>();
        List<Map<String, Object>> patentList = new ArrayList<Map<String, Object>>();
        //查询total
        int total = patentRepository.findAnCountNew();
        pagination.setTotal(total);
        //获取分页信息，封装Pageable
        int size = pagination.getSize();
        int pageNumber = pagination.getStart() / size;
        Pageable pageable = new PageRequest(pageNumber, size);
        //查询结果集
        searchResult = patentRepository.findPatentListNew(pageable);
        //遍历结果集，封装返回案卷列表
        for (int i = 0; i < searchResult.size(); i++) {
            Object[] itemRestlt = searchResult.get(i);
            Map<String, Object> temporaryMap = new HashMap<String, Object>();
            temporaryMap.put("an", itemRestlt[0].toString().substring(0, searchResult.get(i)[0].toString().indexOf(".")));
            temporaryMap.put("citedAn", itemRestlt[2] == null ? "" : searchResult.get(i)[2].toString());
            temporaryMap.put("citeType", itemRestlt[3] == null ? "" : searchResult.get(i)[3].toString());
            temporaryMap.put("apIpc", itemRestlt[1] == null ? "" : searchResult.get(i)[4].toString());
            temporaryMap.put("cIpc", itemRestlt[4] == null ? "" : searchResult.get(i)[5].toString());
            temporaryMap.put("apoldAn", itemRestlt[0].toString());
            temporaryMap.put("location", itemRestlt[5] == null ? "" : searchResult.get(i)[5].toString());
            patentList.add(temporaryMap);
        }
        resultMap.put("anList", patentList);
        resultMap.put("pagination", pagination);
        return resultMap;
    }

    /**
     * 到CNABS或CNTXT查询结果集，返回
     * 传参：选库，检索式，显示字段拼接的字符串 ,分页对象
     */
    public TrsResult searchTRS(String db, String exp, String fields, Pagination page) {
        Condition condition = new Condition();
        condition.setExp(exp);
        //选库
        condition.setDbName(db);
        //显示字段 :申请号 标题 公开日 主分类
        condition.setDisplayFields(fields);
        //排序:降序
        //condition.setSortFields("-"+"pd");
        condition.setPagination(page);
        TrsResult tr = trsEngine.search(condition);
        return tr;
    }

    /**
     * 到CNABS或CNTXT查询结果集，返回
     * 传参：选库，检索式，显示字段拼接的字符串 ,没有分页对象
     */
    public TrsResult searchTRS(String db, String exp, String fields) {
        Condition condition = new Condition();
        condition.setExp(exp);
        //选库
        condition.setDbName(db);
        //显示字段 :申请号 标题 公开日 主分类
        condition.setDisplayFields(fields);
        TrsResult tr = trsEngine.search(condition);
        return tr;
    }

    /**
     * 到trs CNABS查询一个号  封装拼接检索式及结果集
     */
    public Map<String, String> searchTrsToCNABS(String an) {
        Map<String, String> patentMap = new HashMap<String, String>();
        Condition condition = new Condition();
        String searchAn = "nrd_an=('" + an + "')";
        condition.setExp(searchAn);
        condition.setDbName(Constant.CNABS_DB);
        condition.setDisplayFields(Constant.GK_PN + "," + Constant.GK_FIELDS + "," + Constant.SQ_FIELDS + "," + Constant.OTHER_FIELDS);
        TrsResult tr = trsEngine.search(condition);
        List<Record> recordList = tr.getRecords();
        int size = recordList.size();
        for (int i = 0; i < size; i++) {
            Map<String, String> assembleData = AssembleData(recordList.get(i).getDataMap());
            if (assembleData.get("NRD_AN").equals(an)) {
                patentMap = assembleData;
            }
        }
        return patentMap;
    }

    /**
     * 到trs CNTXT查询一个号 封装拼接检索式
     */
    public Map<String, String> searchTrsToCNTXT(String an) {
        Map<String, String> patentMap = new HashMap<String, String>();
        Condition condition = new Condition();
        String searchAn = "nrd_an=('" + an + "')";
        condition.setExp(searchAn);
        condition.setDbName(Constant.CNTXT_DB);
        condition.setDisplayFields(Constant.CLMS + "," + Constant.DESC + "," + Constant.CNTXT_AN);
        TrsResult tr1 = trsEngine.search(condition);
        List<Record> recordList = tr1.getRecords();
        for (int i = 0; i < recordList.size(); i++) {
            Map<String, String> temp = recordList.get(i).getDataMap();
            if (temp.get("NRD_AN").equals(an)) {
                patentMap = temp;
            }
        }
        return patentMap;
    }

    /**
     * 到trs CNABS查询两个号  封装拼接检索式
     */
    public Map<String, Map<String, String>> searchTrsToCNABS(String an, String citedAn) {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        Map<String, String> patentInfoMap = new HashMap<String, String>();
        Map<String, String> citeInfoMap = new HashMap<String, String>();
        Condition condition = new Condition();
        String searchAn = "nrd_an=('" + an + "' or '" + citedAn + "')";//nrd_an=( 'CN201510493315' or 'CN01128416')
        condition.setExp(searchAn);
        condition.setDbName(Constant.CNABS_DB);
        condition.setDisplayFields(Constant.GK_PN + "," + Constant.GK_FIELDS + "," + Constant.SQ_FIELDS + "," + Constant.OTHER_FIELDS);
        TrsResult tr = trsEngine.search(condition);
        List<Record> recordList = tr.getRecords();
        int size = recordList.size();
        for (int i = 0; i < size; i++) {
            Map<String, String> assembleData = AssembleData(recordList.get(i).getDataMap());
            if (assembleData.get("NRD_AN").equals(an)) {
                patentInfoMap = assembleData;
            } else if (assembleData.get("NRD_AN").equals(citedAn)) {
                citeInfoMap = assembleData;
            }
        }
        map.put("patentInfoMap", patentInfoMap);
        map.put("citedInfoMap", citeInfoMap);
        return map;
    }

    /**
     * 到trs CNTXT查询两个号 封装拼接检索式
     */
    public Map<String, Map<String, String>> searchTrsToCNTXT(String an, String citedAn) {
        Map map = new HashMap<String, Map<String, String>>();
        String searchAn = "nrd_an=('" + an + "' or '" + citedAn + "')";//nrd_an=( 'CN201510493315' or 'CN01128416')
        Condition conditionCNTXT = new Condition();
        conditionCNTXT.setExp(searchAn);
        conditionCNTXT.setDbName(Constant.CNTXT_DB);
        conditionCNTXT.setDisplayFields(Constant.CLMS + "," + Constant.DESC + "," + Constant.CNTXT_AN);
        //2.2 查询
        TrsResult tr1 = trsEngine.search(conditionCNTXT);
        //2.3 获取结果集
        List<Record> recordList1 = tr1.getRecords();
        Map<String, String> selfTemporaryMap = new HashMap<String, String>();
        Map<String, String> citeTemporaryMap = new HashMap<String, String>();
        for (int i = 0; i < recordList1.size(); i++) {
            Map<String, String> temp = recordList1.get(i).getDataMap();
            if (temp.get("NRD_AN").equals(an)) {
                selfTemporaryMap = temp;
            } else if (temp.get("NRD_AN").equals(citedAn)) {
                citeTemporaryMap = temp;
            }
        }
        map.put("selfTemporaryMap", selfTemporaryMap);
        map.put("citeTemporaryMap", citeTemporaryMap);
        return map;
    }

    public List sortByTokenFrequence(List sourceList) {
        Collections.sort(sourceList, new TokenComparator());
        return sourceList;
    }

    public class TokenComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Token token1 = (Token) o1;
            Token token2 = (Token) o2;
            if (token1.getFreq() > token2.getFreq())
                return -1;
            if (token1.getFreq() < token2.getFreq())
                return 1;
            return 0;
        }
    }

    public List filterListByTokenName(List source) {
        List target = new ArrayList();
        int size = source.size();
        if (source == null || size < 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            Token token = (Token) source.get(i);
            int length = token.getWord().length();
            if (length >= 2) {
                target.add(token);
            }
        }
        return target;
    }

    /**
     * 默认取GK（公开）字段数据，若为空，则取SQ（授权）字段数据
     *
     * @param dataMap 检索出的trs行数据
     * @return 组装后的数据
     */
    public Map<String, String> AssembleData(Map<String, String> dataMap) {
        Map<String, String> resultMap = new HashMap<String, String>();
        String gkPn = dataMap.get(Constant.GK_PN);
        String prefix = Constant.GK_PREFIX;
        String ap = dataMap.get(Constant.CNTXT_AN).split(" ")[0];
        String PD = dataMap.get(Constant.PD);
        String ipcMain = dataMap.get(Constant.IPC_MAIN);
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
        resultMap.put("Ap", ap);
        resultMap.put("PD", PD);
        resultMap.put("ipcMain", ipcMain);
        return resultMap;
    }

    /**
     * 输入一段文本拆词接口
     */
    public List getSplitwordList(String text) {
        List result = new ArrayList();
        try {
            result = ThkAnalyzer.getInstance().analysis(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 标引词保存到redis
     */
    public boolean saveMarkToRedis(String userId, String an, List markList, int patenttype) {
        Gson gson = new Gson();
        Map<String, String> markMap = new HashMap<String, String>();
        String timark = "";
        String othersmark = "";
        List<ZKPatentMark> zkPatentMarkList = markList;
        // String an=zkPatentMarkList.get(0).getAn();
        //Jedis jedis= JedisPoolUtilSingle.getJedis();
        JedisCluster jedis = JedisPoolUtil.getJedis();
        for (int i = 0; i < zkPatentMarkList.size(); i++) {
            ZKPatentMark item = zkPatentMarkList.get(i);
            String type = item.getType();
            if (type.equals("1")) {
                timark += item.getWord() + ",";
            } else if (type.equals("2")) {
                othersmark += item.getWord() + ",";
            }
        }
        if (timark.length() != 0) {
            String timarkResult = timark.substring(0, timark.length() - 1);
            markMap.put("zkTiWord", timarkResult);
        } else {
            markMap.put("zkTiWord", "");
        }
        if (othersmark.length() != 0) {
            String clmsmarkResult = othersmark.substring(0, othersmark.length() - 1);
            markMap.put("zkOthersWord", clmsmarkResult);
        } else {
            markMap.put("zkOthersWord", "");
        }
        markMap.put("zkAn", an);
        markMap.put("zkType", Integer.toString(patenttype));
        jedis.set("zk" + an, gson.toJson(markMap));
        //JedisPoolUtil.closeJedis(jedis);
        return true;
    }

    /**
     * 查询标引词从redis
     */
    public List<ZKPatentMark> searchMarkFromRedis(String an) {
        Gson gson = new Gson();
        List<ZKPatentMark> list = new ArrayList<ZKPatentMark>();
        JedisCluster jedis = JedisPoolUtil.getJedis();
        //Jedis jedis= JedisPoolUtilSingle.getJedis();
        String mark = jedis.get("zk" + an);
        Map markMap = gson.fromJson(mark, Map.class);
        if (markMap != null) {
            String tiMarkWord = markMap.get("zkTiWord").toString();
            String othersMarkWord = markMap.get("zkOthersWord").toString();
            // String descMarkWOrd=markMap.get("zkDescWord").toString();
            int zkType = Integer.parseInt(markMap.get("zkType").toString());
            String zkAn = markMap.get("zkAn").toString();
            if (tiMarkWord.length() != 0) {
                String[] ti = tiMarkWord.split(",");
                for (int i = 0; i < ti.length; i++) {
                    ZKPatentMark timark = new ZKPatentMark();
                    timark.setAn(zkAn);
                    timark.setType("1");
                    timark.setInv_type(zkType);
                    timark.setWord(ti[i]);
                    list.add(timark);
                }
            }
            if (othersMarkWord.length() != 0) {
                String[] others = othersMarkWord.split(",");
                for (int i = 0; i < others.length; i++) {
                    ZKPatentMark othermark = new ZKPatentMark();
                    othermark.setAn(zkAn);
                    othermark.setType("2");
                    othermark.setWord(others[i]);
                    othermark.setInv_type(zkType);
                    list.add(othermark);
                }
            }
        }
        //JedisPoolUtilSingle.closeJedis(jedis);
        return list;
    }

    /**
     * 保存标引词到数据库
     */
    public boolean saveMarkToOracle(String userId, String an,List markList,int patenttype) {
        List<ZKPatentMark> zkPatentMarkList = markList;
        Map<String,String> markMap = new HashMap<String,String>();
        String timark = "";
        String othersmark = "";
        String timarkResult="";
        String othersmarkResult="";
        for(int i=0;i< zkPatentMarkList.size();i++){
            ZKPatentMark item = zkPatentMarkList.get(i);
            String type = item.getType();
            if(type.equals("1")){
                timark += item.getWord()+",";
            }else if(type.equals("2")){
                othersmark += item.getWord()+",";
            }
        }
        if(timark.length()!=0){
            timarkResult=timark.substring(0,timark.length()-1);
        }else{
            timarkResult="";
        }
        if(othersmark.length()!=0){
            othersmarkResult=othersmark.substring(0,othersmark.length()-1);
        }else{
            othersmarkResult="";
        }
        int flag;
        if(zkPatentRepository.findZKMarksByAn(an).size()!=0){
            flag=zkPatentRepository.updateZKMark(an,timarkResult,othersmarkResult,patenttype);
        }else {
            flag=zkPatentRepository.saveKeyWord(an,timarkResult,othersmarkResult,patenttype);
        }
        return (flag>0) ? true : false;
    }

    /**
     * 从数据库中查询标引词
     */
    public List<ZKPatentMark> searchMarkFromOracle(String an) {
        List<ZKPatentMark> list=new ArrayList<ZKPatentMark>();
        List<Object[]> resultold = zkPatentRepository.findZKMarksByAn(an);
        List<ZKPatentMark> tiKeywordList = new ArrayList<>();
        List<ZKPatentMark> otherKeywordList = new ArrayList<>();
        if(resultold !=null && resultold.size()>0){
            Object[]  result=resultold.get(0);
            String tiMarkWord= (String) result[1];
            tiMarkWord = tiMarkWord.replace( " ", "");
            String othersMarkWord= (String) result[2];
            othersMarkWord = othersMarkWord.replace( " ", "");
            String zkAn= (String) result[0];
            int invtype= Integer.valueOf(result[3].toString());
            if(tiMarkWord.length()!=0){
                tiKeywordList = handleKeywordsToList(zkAn,invtype, tiMarkWord, "1");
                list.addAll(tiKeywordList);
            }
            if(othersMarkWord.length()!=0){
                otherKeywordList = handleKeywordsToList(zkAn,invtype, othersMarkWord, "2");
                list.addAll(otherKeywordList);
            }
        }
        return list;
    }
    private List<ZKPatentMark> handleKeywordsToList(String an, int invType, String keywords, String tiOrOtherType){
        List<ZKPatentMark> list=new ArrayList<ZKPatentMark>();
        String[] tempKeywords= keywords.split(",");
        for(int i=0;i<tempKeywords.length;i++){
            ZKPatentMark timark=new ZKPatentMark();
            timark.setInv_type(invType);
            timark.setAn(an);
            timark.setType(tiOrOtherType);
            timark.setWord(tempKeywords[i]);
            list.add(timark);
        }
        return list;
    }
    /**
     * 保存错误的分词
     * @param errorKeyword
     * @return
     */
    public boolean removeErrorKeyword(String errorKeyword) {
        int flag = zkPatentRepository.saveErrorKeyWord(errorKeyword);
        return (flag>0) ? true : false;
    }

}