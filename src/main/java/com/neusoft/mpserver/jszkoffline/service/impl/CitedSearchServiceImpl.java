package com.neusoft.mpserver.jszkoffline.service.impl;
import com.neusoft.mpserver.common.domain.Condition;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.domain.Record;
import com.neusoft.mpserver.common.domain.TrsResult;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.common.util.XmlFormatter;
import com.neusoft.mpserver.jszkoffline.dao.CitedInfoRepository;
import com.neusoft.mpserver.jszkoffline.service.CitedSearchService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 查询申请案卷及对比文献的详细信息
 */
@Service
public class CitedSearchServiceImpl implements CitedSearchService {

    @Autowired
    private TrsEngine trsEngine;
    @Autowired
    private CitedInfoRepository citedInfoRepository;

    @Override
    public Map<String, Object> searchPatentList(String ipc, Pagination pagination) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object[]> searchResult = new ArrayList<Object[]>();
        List<Map<String,Object>>  anList=new ArrayList<Map<String,Object>>();
        //查询total
        int total=citedInfoRepository.findAnCount(ipc);
        pagination.setTotal(total);
        //获取分页信息，封装Pageable
        int size=pagination.getSize();
        int pageNumber = pagination.getStart() /size;
        Pageable pageable = new PageRequest(pageNumber, size);
        searchResult=citedInfoRepository.findByIpcMain(ipc,pageable);
        //遍历结果集，到trs查询详细信息
        for(int i=0;i<searchResult.size();i++){
            Map<String,Object>  anAndCitedMap=new HashMap<String,Object>();
             String apoldAn=searchResult.get(i)[0].toString();
             String apAn=searchResult.get(i)[0].toString().substring(0,searchResult.get(i)[0].toString().indexOf("."));
            String citedAn=searchResult.get(i)[2] == null ?"" :searchResult.get(i)[2].toString() ;
            String citedType=searchResult.get(i)[3] ==null ? "":searchResult.get(i)[3].toString() ;
            String pIpc=searchResult.get(i)[4] == null ?"" :searchResult.get(i)[4].toString() ;
            String cIpc=searchResult.get(i)[5] == null ?"" :searchResult.get(i)[5].toString() ;
            String location=searchResult.get(i)[6] == null ?"" :searchResult.get(i)[6].toString() ;
            anAndCitedMap.put("an",apAn);
            anAndCitedMap.put("citedAn",citedAn);
            anAndCitedMap.put("citeType",citedType);
            anAndCitedMap.put("apIpc",pIpc);
            anAndCitedMap.put("cIpc",cIpc);
            anAndCitedMap.put("location",location);
            anAndCitedMap.put("apoldAn",apoldAn);
            anList.add(anAndCitedMap);
        }
        map.put("anList",anList);
        map.put("pagination",pagination);
        return map;
    }

    /**
     * 查询申请专利和对比文献专利的详细信息
     * @param an
     * @param citedAn
     * @return
     */
    @Override
    public Map<String, Object> searchPatentDetailInfo(String an, String citedAn) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> patentBaseInfoMap= new HashMap<String,String>();
        Map<String, String> citeBaseInfoMap= new HashMap<String,String>();
        Condition condition = new Condition();
        Condition condition1 = new Condition();
        String searchAn = "nrd_an=('" + an + "' or '"+citedAn+"')";//nrd_an=( 'CN201510493315' or 'CN01128416')
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
            }else if(assembleData.get("NRD_AN").equals(citedAn)){
                citeBaseInfoMap=assembleData;
            }
        }
        //查询权利要求及说明书,摘要
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
            }else if(temp.get("NRD_AN").equals(citedAn)){
                citeMap=temp;
            }
        }
        // String clms1=XmlFormatter.format(selfMap.get("CLMS"), XmlFormatter.XmlType.CLMS);
        //String desc = XmlFormatter.format(selfMap.get("DESC1"), XmlFormatter.XmlType.DESC);
        //String clm1new=XmlFormatter.getClms1(clms1);
        //System.out.println("第一段权利要求为："+clm1new);
        //System.out.println("技术领域为："+XmlFormatter.getPartOfDesc(desc, "背景技术"));


        patentBaseInfoMap.put("CLIMS", XmlFormatter.format(selfMap.get("CLMS"), XmlFormatter.XmlType.CLMS));
        patentBaseInfoMap.put("DESC", XmlFormatter.format(selfMap.get("DESC1"), XmlFormatter.XmlType.DESC));
        citeBaseInfoMap.put("CLIMS", XmlFormatter.format(citeMap.get("CLMS"), XmlFormatter.XmlType.CLMS));
        citeBaseInfoMap.put("DESC", XmlFormatter.format(citeMap.get("DESC1"), XmlFormatter.XmlType.DESC));

        //patentBaseInfoMap.put("CLIMS",(selfMap.get("CLMS")).replaceAll("<[^>]+>","").replaceAll("(\\.|。|;|；)\n", "。<br/>").replace("\n",""));
        // patentBaseInfoMap.put("DESC",(selfMap.get("DESC1")).replaceAll("<[^>]+>","").replaceAll("(\\.|。|;|；)\n", "。<br/>").replace("\n","<span></span>").replace("技术领域<span></span>","<div class='desc-title' style='color:#409EFF'>技术领域</div>").replace("背景技术<span></span>","<div class='desc-title' style='color:#409EFF'>背景技术</div>").replace("发明内容<span></span>","<div class='desc-title' style='color:#409EFF'>发明内容</div>").replace("附图说明<span></span>","<div class='desc-title' style='color:#409EFF'>附图说明</div>").replace("具体实施方式<span></span>","<div class='desc-title' style='color:#409EFF'>具体实施方式</div>"));
        //citeBaseInfoMap.put("CLIMS",(citeMap.get("CLMS")).replaceAll("<[^>]+>","").replaceAll("(\\.|。|;|；)\n", "。<br/>").replace("\n",""));
        //citeBaseInfoMap.put("DESC",(citeMap.get("DESC1")).replaceAll("<[^>]+>","").replaceAll("(\\.|。|;|；)\n", "。<br/>").replace("\n","<span></span>").replace("技术领域<span></span>","<div class='desc-title' style='color:#409EFF'>技术领域</div>").replace("背景技术<span></span>","<div class='desc-title' style='color:#409EFF'>背景技术</div>").replace("发明内容<span></span>","<div class='desc-title' style='color:#409EFF'>发明内容</div>").replace("附图说明<span></span>","<div class='desc-title' style='color:#409EFF'>附图说明</div>").replace("具体实施方式<span></span>","<div class='desc-title' style='color:#409EFF'>具体实施方式</div>"));
        map.put("thispatentBaseInfo", patentBaseInfoMap);
        map.put("citepatentBaseInfo", citeBaseInfoMap);
        return map;
    }

    /**
     * 无引文 申请号
     * @param an
     * @return
     */

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
/*    public static void  main(String[] args) {
        String str = ".\n \n daldfjalkd。\n";
        System.out.println(str.replaceAll("(\\.|。)\n", "<br/>").replace("\n", ""));
    }*/



}
