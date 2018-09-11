package com.neusoft.mpserver.sipo57.service.impl;

import com.neusoft.mpserver.common.domain.Condition;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.domain.Record;
import com.neusoft.mpserver.common.domain.TrsResult;
import com.neusoft.mpserver.common.engine.TrsEngine;
import com.neusoft.mpserver.sipo57.dao.IpcMarkRepository;
import com.neusoft.mpserver.sipo57.domain.Constant;
import com.neusoft.mpserver.sipo57.service.IpcSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * ic业务查询模块：service层实现
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@Service
public class IpcSearchServiceImpl implements IpcSearchService {
    @Autowired
    private TrsEngine trsEngine;
    @Autowired
    private IpcMarkRepository markRerpository;
    @Override
    public Map<String, Object> searchPatentList(String ipc, Pagination pagination) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, String>> patentList = null;
        List markAn=null;
        Condition condition = new Condition();
        String searchIc = "IPC_MAIN='" + ipc + "'";
        condition.setExp(searchIc);
        condition.setDbName(Constant.CNABS_DB);
        condition.setDisplayFields(Constant.GK_PN + "," + Constant.GK_FIELDS + "," + Constant.SQ_FIELDS + "," + Constant.OTHER_FIELDS);
        condition.setPagination(pagination);
        TrsResult tr = trsEngine.search(condition);
        List<Record> recordList = tr.getRecords();
        int size = recordList.size();
        if (size > 0) {
            patentList = new ArrayList<Map<String, String>>();
            //String resultAn="";
            for (int i = 0; i < size; i++) {
                String resultAn="";
                Map<String, String> assembleData=AssembleData(recordList.get(i).getDataMap());
                String an=assembleData.get("NRD_AN");
                if(i== size-1){
                    resultAn +=an;
                }else{
                    resultAn +=an+",";
                }
                List anList=markRerpository.matchMarkByAn(Arrays.asList(resultAn.split(",")));
                if(anList.contains(an)){
                    assembleData.put("marked","true");
                }else{
                    assembleData.put("marked","false");
                }
                patentList.add(assembleData);
            }
        }
        map.put("patentList", patentList);
        map.put("pagination", tr.getPagination());
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

    @Override
    public Map<String, String> searchIpc(String ipc) {
        Map<String, String> ipcResult = null;
        Condition condition = new Condition();
        String searchIc = "IC='" + ipc + "'";
        condition.setExp(searchIc);
        condition.setDbName(Constant.IPC_DB);
        condition.setDisplayFields(Constant.IPC_FIELDS);
        TrsResult tr = trsEngine.search(condition);
        List<Record> recordList = tr.getRecords();
        if (recordList.size() > 0) {
            ipcResult = new HashMap<String, String>();
            for (int i = 0; i < 1; i++) {
                ipcResult = recordList.get(i).getDataMap();
            }
        }
        return ipcResult;
    }
}
