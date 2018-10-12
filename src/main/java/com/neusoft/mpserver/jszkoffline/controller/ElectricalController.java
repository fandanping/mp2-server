package com.neusoft.mpserver.jszkoffline.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.domain.ElectrialTiMark;
import com.neusoft.mpserver.jszkoffline.service.CitedSearchService;
import com.neusoft.mpserver.jszkoffline.service.ElectricalService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import com.neusoft.mpserver.sipo57.domain.IpcMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *查询案卷列表：本申请专利信息
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@RestController
@RequestMapping("/electrical")
public class ElectricalController {
    @Autowired
    private ElectricalService electricalService;

    //查询案卷申请号列表
    @GetMapping("/search/list")
    public Map<String, Object> searchPatentList( Pagination pagination, String token) {
        Map<String, Object> patentMap = electricalService.searchCompareFilePatentList(pagination);
        return patentMap;
    }

    //查询申请案卷的详细信息
    @GetMapping("/search/list/{an}")
    public Map<String,Object> searchPatentDetailInfo(@PathVariable String an, String token){
        Map<String, Object> patentMap =new HashMap<String,Object>();
        patentMap = electricalService.searchPatentDetailInfo(an);
        return patentMap;
    }

    //查询显示标引词
    @GetMapping("/list/{an}/{citedAn}")
    public Map<String, List<ElectrialTiMark>> markList (@PathVariable String an,@PathVariable String citedAn, HttpServletRequest request){
        Map<String, List<ElectrialTiMark>> result = new HashMap<String, List<ElectrialTiMark>>();
        String userId = (String) request.getAttribute(Constant.USER_ID);
        result.put("markTiList", electricalService.showMarkList(an,citedAn, userId));
        return result;
    }

    //保存标引词
    @PostMapping("/mark/add")
    public  Map<String ,Object> addMark(@RequestBody Map postMap, HttpServletRequest request){
        Gson gson = new Gson();
        String markStr = (String) postMap.get("markList");
        ArrayList<ElectrialTiMark> markList = gson.fromJson(markStr, new TypeToken<List<ElectrialTiMark>>(){}.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag=electricalService.addMark(userId,markList);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }

    //删除标引词
    @PostMapping("/mark/delete")
    public Map<String,Object>  deleteMark(@RequestBody Map postMap,HttpServletRequest request){
        String marks = (String) postMap.get("marks");
        Gson gson = new Gson();
        ArrayList<ElectrialTiMark> markList = gson.fromJson(marks, new TypeToken<List<ElectrialTiMark>>(){}.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag= electricalService.deleteMark(markList);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }
    //查询标题拆词
    @GetMapping("/chaici/list/{ti}")
    public Map<String, List> chaiciList (@PathVariable String ti){
        Map<String, List> result = new HashMap<String, List>();
        result.put("chaiCiList", electricalService.showChaiCiList(ti));
        return result;
    }



}
