package com.neusoft.apserver.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.apserver.domain.AddressMark;
import com.neusoft.apserver.domain.Constant;
import com.neusoft.apserver.domain.IpcMark;
import com.neusoft.apserver.service.AddressSearchService;
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
        String markStr = (String) postMap.get("markAddressListBODY");
        ArrayList<AddressMark> markList = gson.fromJson(markStr, new TypeToken<List<AddressMark>>(){}.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag=addressSearchService.addMark(userId,markList);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }





}
