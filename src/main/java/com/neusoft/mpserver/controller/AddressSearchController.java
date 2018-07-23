package com.neusoft.mpserver.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.mpserver.domain.AddressMark;
import com.neusoft.mpserver.domain.Constant;
import com.neusoft.mpserver.service.AddressSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * address:查询未标引地址列表模块
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@RestController
@RequestMapping("/address")
public class AddressSearchController {

    @Autowired
    private AddressSearchService addressSearchService;

    /**
     * 查询正在标引的地址列表：controller层
     * @param request
     * @return
     */
    @GetMapping("/search/marking")
    public Map<String,List<AddressMark>> searchMarking(HttpServletRequest request){
        String userId = (String) request.getAttribute(Constant.USER_ID);
        Map<String ,List<AddressMark>> result = new HashMap<String ,List<AddressMark>>();
        result.put("addressMarkList",addressSearchService.showMarkingList(userId));
        return result;
    }

    /**
     * 点击随标按钮，随机20篇出来
     * @param keyword
     * @param request
     * @return
     */
    @GetMapping("/search/random")
     public Map<String,List<AddressMark>>  searchUnMark(String keyword,HttpServletRequest request){
        String userId = (String) request.getAttribute(Constant.USER_ID);
        Map<String ,List<AddressMark>> result = new HashMap<String ,List<AddressMark>>();
        result.put("addressMarkList",addressSearchService.showUnMarkList(userId,keyword));
        return result;
    }

    @PostMapping("/save")
    public Map<String ,Object> addAddress(@RequestBody Map postMap, HttpServletRequest request){
        Gson gson = new Gson();
        //[{id=33, marked=1, province=aa, city=aa, area=, town=as, status=1}]
        //[{"type":2,"word":"张长峰","userId":"DA647245B897401E99D8BEE7C9111EB2","ipc":"A61K36/28","an":"CN201711297848"}]
        String markStr = (String) postMap.get("markAddressList");
        System.out.println(markStr);
        List<AddressMark> markList = gson.fromJson(markStr, new TypeToken<List<AddressMark>>(){}.getType());

        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag=addressSearchService.addMark(userId,markList);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }





}
