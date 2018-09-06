package com.neusoft.mpserver.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.mpserver.domain.AddressMarkForm;
import com.neusoft.mpserver.domain.AddressRule;
import com.neusoft.mpserver.domain.AddressRuleForm;
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
     *
     * @param request
     * @return
     */
    @GetMapping("/search/marking")
    public Map<String, Object> searchMarking(HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constant.USER_ID);
        return addressSearchService.showMarkingList(userId);
    }

    /**
     * 点击随标按钮，随机20篇出来，也可通过关键词模糊匹配
     *
     * @param keyword
     * @param request
     * @return
     */
    @GetMapping("/search/random")
    public Map<String, Object> searchUnMark(String keyword, HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constant.USER_ID);
        return addressSearchService.showUnMarkList(userId, keyword);
    }

    /**
     * 保存词
     *
     * @param postMap
     * @param request
     * @return
     */
    @PostMapping("/save")
    public Map<String, Object> addAddress(@RequestBody Map postMap, HttpServletRequest request) {
        Gson gson = new Gson();
        String markStr = (String) postMap.get("markAddressList");
        List<AddressMarkForm> markList = gson.fromJson(markStr, new TypeToken<List<AddressMarkForm>>() {
        }.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        //...
        String ruleStr = (String) postMap.get("addressRuleList");
        List<AddressRuleForm> ruleList = gson.fromJson(ruleStr, new TypeToken<List<AddressRuleForm>>() {
        }.getType());
        boolean flag = addressSearchService.addMark(userId, markList, ruleList);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("flag", flag);
        return map;
    }

    /**
     * 查询所有标引规则，不带分页，用于前台正则判断，匹配规则的页面回填内容
     *
     * @return
     */
    @GetMapping("/rule/list")
    public Map<String, Object> getRuleList(HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constant.USER_ID);
        return addressSearchService.showRuleList();
    }

    /**
     * 查询标引规则，带分页，用于展示今天创建的规则
     *
     * @param type
     * @param keyword
     * @param pageNumber
     * @param size
     * @param request
     * @return
     */
    @GetMapping("/rule/list/page")
    public Map<String, Object> getRulePageList(String type, String keyword, int pageNumber, int size, HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constant.USER_ID);
        return addressSearchService.showRulePageList(userId, type, keyword, pageNumber, size);
    }

    /**
     * 用于修改规则，只能修改自己的规则
     *
     * @param postMap
     * @param request
     * @return
     */
    @PostMapping("/rule/modify")
    public Map<String, Object> modifyRule(@RequestBody Map postMap, HttpServletRequest request) {
        Gson gson = new Gson();
        String ruleStr = (String) postMap.get("addressRule");
        AddressRule rule = gson.fromJson(ruleStr, new TypeToken<AddressRule>() {
        }.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag = addressSearchService.ModifyRule(userId, rule);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("flag", flag);
        return map;
    }

    /**
     * 根据ID删除标引规则
     * @param postMap
     * @param request
     * @return
     */
    @PostMapping("/rule/delete")
    public Map<String, Object> deleteRule(@RequestBody Map postMap, HttpServletRequest request) {
        String id = (String) postMap.get("id");
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag = addressSearchService.deleteRule(id, userId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("flag", flag);
        return map;
    }
}
