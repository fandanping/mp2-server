package com.neusoft.mpserver.controller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.mpserver.domain.AddressMark;
import com.neusoft.mpserver.domain.AddressMarkForm;
import com.neusoft.mpserver.domain.Constant;
import com.neusoft.mpserver.service.AddressSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
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
     * 登录后初次进首页，查询正在标引的地址列表，如果没有正在标引的，则随机20篇：controller层
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
     * 点击随标按钮，随机20篇出来，也可通过关键词模糊匹配
     * @param keyword
     * @param request
     * @return
     */
    @GetMapping("/search/random")
     public Map<String,Object>  searchUnMark(String keyword,HttpServletRequest request){
        String userId = (String) request.getAttribute(Constant.USER_ID);
       // Map<String ,List<AddressMark>> result = new HashMap<String ,List<AddressMark>>();
       // result.put("addressMarkList",addressSearchService.showUnMarkList(userId,keyword));
        return addressSearchService.showUnMarkList(userId,keyword);
    }

    /**
     * 保存词
     * @param postMap
     * @param request
     * @return
     */
    @PostMapping("/save")
    public Map<String ,Object> addAddress(@RequestBody Map postMap, HttpServletRequest request){
        Gson gson = new Gson();
        String markStr = (String) postMap.get("markAddressList");
        List<AddressMarkForm> markList = gson.fromJson(markStr, new TypeToken<List<AddressMarkForm>>(){}.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag=addressSearchService.addMark(userId,markList);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }





}
