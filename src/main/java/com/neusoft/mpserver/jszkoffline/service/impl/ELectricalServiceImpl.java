package com.neusoft.mpserver.jszkoffline.service.impl;
import com.neusoft.mpserver.common.domain.Condition;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.domain.Record;
import com.neusoft.mpserver.common.domain.TrsResult;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.common.util.IDGenerator;
import com.neusoft.mpserver.common.util.XmlFormatter;
import com.neusoft.mpserver.jszkoffline.dao.ElectrialMarkRepository;
import com.neusoft.mpserver.jszkoffline.dao.ElectrialRepository;
import com.neusoft.mpserver.jszkoffline.domain.ElectrialTiMark;
import com.neusoft.mpserver.jszkoffline.service.ElectricalService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thk.analyzer.ThkAnalyzer;
import thk.analyzer.Token;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ELectricalServiceImpl  implements ElectricalService {
    @Autowired
    private TrsEngine trsEngine;
    @Autowired
    private ElectrialRepository electrialRepository;
    @Autowired
    private ElectrialMarkRepository electrialMarkRepository;

    /**
     * 查询案卷列表
     * @param pagination
     * @return
     */
    @Override
    public Map<String, Object> searchPatentList(Pagination pagination) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object[]> searchResult = new ArrayList<Object[]>();
        List<Map<String,Object>>  patentList=new ArrayList<Map<String,Object>>();
        //查询total
        int total=electrialRepository.findAnCount();
        pagination.setTotal(total);
        //获取分页信息，封装Pageable
        int size=pagination.getSize();
        int pageNumber = pagination.getStart() /size;
        Pageable pageable = new PageRequest(pageNumber, size);
        //查询列表
        searchResult=electrialRepository.findByDetail(pageable);
        for(int i=0;i<searchResult.size();i++){
            Map<String,Object>  patentListmap=new HashMap<String,Object>();
            String oldAn=searchResult.get(i)[0].toString();
            String an=searchResult.get(i)[0].toString().substring(0,searchResult.get(i)[0].toString().indexOf("."));
            String pIpcMain=searchResult.get(i)[2] == null ?"" :searchResult.get(i)[2].toString() ;
            String pn =searchResult.get(i)[3] == null ?"" :searchResult.get(i)[3].toString() ;
            String referenceCategory =searchResult.get(i)[4] == null ?"" :searchResult.get(i)[4].toString() ;
            String citedAn =searchResult.get(i)[5] == null ?"" :searchResult.get(i)[5].toString() ;
            patentListmap.put("oldan",oldAn);
            patentListmap.put("an",an);
            patentListmap.put("pIpcMain",pIpcMain);
            patentListmap.put("pn",pn);
            patentListmap.put("referenceCategory",referenceCategory);
            patentListmap.put("citedAn",citedAn);
            patentList.add(patentListmap);
        }
        map.put("patentList",patentList);
        map.put("pagination",pagination);
        return map;
    }

    /**
     * 查询案卷详细信息
     * @param an
     * @return
     */
    @Override
    public Map<String, Object> searchPatentDetailInfo(String an) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> patentInfoMap= new HashMap<String,String>();
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
                patentInfoMap=assembleData;
            }
        }
        //查询权利要求及说明书
        condition1.setExp(searchAn);
        condition1.setDbName(Constant.CNTXT_DB);
        condition1.setDisplayFields(Constant.CLMS + "," + Constant.DESC + "," + Constant.CNTXT_AN);
        TrsResult tr1 = trsEngine.search(condition1);
        List<Record> recordList1 = tr1.getRecords();
        Map<String, String> selfMap=new HashMap<String,String>();
        for(int i=0;i<recordList1.size();i++){
            Map<String, String> temp=recordList1.get(i).getDataMap();
            if(temp.get("NRD_AN").equals(an)){
                selfMap=temp;
            }
        }
        patentInfoMap.put("CLIMS", XmlFormatter.format(selfMap.get("CLMS"), XmlFormatter.XmlType.CLMS));
        patentInfoMap.put("DESC", XmlFormatter.format(selfMap.get("DESC1"), XmlFormatter.XmlType.DESC));
        map.put("patentInfo", patentInfoMap);
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
     * 查询标引词
     * @param an
     * @return
     */
    @Transactional
    @Override
    public List<ElectrialTiMark> showMarkList(String an) {
        List<ElectrialTiMark> markList = electrialMarkRepository.findTIMarkByAn(an);
        //增加一个案卷对应一行标引的记录 start
        List<ElectrialTiMark> result =new ArrayList<ElectrialTiMark>();
         for(int i=0;i<markList.size();i++){
             String word=markList.get(i).getWord();
             String[] wordarr = word.split(",");
             for(int j=0;j<wordarr.length;j++){
                 ElectrialTiMark el=new ElectrialTiMark();
                 el.setWord(wordarr[j]);
                 el.setUserId(markList.get(i).getUserId());
                 el.setType(markList.get(i).getType());
                 el.setId(markList.get(i).getId());
                 el.setCreateTime(markList.get(i).getCreateTime());
                 el.setAn(markList.get(i).getAn());
                 result.add(el);
             }
         }
        //end
        return result;
    }

    /**
     * 保存标引词  一对一
     * @param userId
     * @param markList
     * @return
     */
    @Transactional
    @Override
    public boolean addMark(String userId, List<ElectrialTiMark> markList) {
       List<ElectrialTiMark> markListResult = markList;
       String id=IDGenerator.generate();
       Date day = new Date();
       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String createTime=df.format(day);
       String usId=markListResult.get(0).getUserId();
       String type=markListResult.get(0).getType();
       String an=markListResult.get(0).getAn();
        String citedAn=markListResult.get(0).getCitedAn();
       String word="";
        for (int i = 0; i < markListResult.size(); i++) {
           if(i==markListResult.size()-1){
                word += markListResult.get(i).getWord();
            }else{
                word += markListResult.get(i).getWord()+",";
            }
        }
       // String trsword="TI="+word;
        List<ElectrialTiMark> list=new ArrayList<ElectrialTiMark>();
        ElectrialTiMark el=new ElectrialTiMark();
        el.setWord(word);
        el.setUserId(usId);
        el.setType(type);
        el.setId(id);
        el.setCreateTime(createTime);
        el.setAn(an);
        el.setCitedAn(citedAn);
        list.add(el);
        if(electrialMarkRepository.findTIMarkByAn(an).isEmpty()){
            if (electrialMarkRepository.saveAll(list).isEmpty()) {
                return false;
            } else {
                return true;
            }
        }else{
            electrialMarkRepository.saveMark(an,word);
            return true;

        }
    }

    /**
     * 删除标引词
     * @return
     */
    @Transactional
    @Override
    public boolean deleteMark(List<ElectrialTiMark> list1) {
        List<ElectrialTiMark> markListResult = list1;
        String an=markListResult.get(0).getAn();
        String word="";
        for (int i = 0; i < markListResult.size(); i++) {
            if(i==markListResult.size()-1){
                word += markListResult.get(i).getWord();
            }else{
                word += markListResult.get(i).getWord()+",";
            }
        }
       // String trsword="TI="+word;
        electrialMarkRepository.saveMark(an,word);
        return true;
    }

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

}
