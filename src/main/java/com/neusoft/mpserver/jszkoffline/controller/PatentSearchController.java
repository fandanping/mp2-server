package com.neusoft.mpserver.jszkoffline.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.domain.wordmark;
import com.neusoft.mpserver.jszkoffline.service.PatentSearchService;
import com.neusoft.mpserver.sipo57.domain.Constant;
import com.neusoft.mpserver.sipo57.domain.IpcMark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *查询案卷列表：包括本申请专利信息和对比文献信息
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@RestController
@RequestMapping("/patent")
public class PatentSearchController {
    @Autowired
    private PatentSearchService patentSearchService;

    //查询案卷申请号列表
    @GetMapping("/search/list")
    public Map<String, Object> searchPatentList( Pagination pagination, String token) throws IOException, SQLException {
        Map<String, Object> patentMap = patentSearchService.searchPatentList(pagination);
        return patentMap;
    }
    //查询 an citedAn查询申请案卷和对比案卷的详细信息
    @GetMapping("/search/list/{an}/{citedAn}")
    public Map<String,Object> searchPatentDetailInfo(@PathVariable String an,@PathVariable String citedAn,String token){
        Map<String, Object> patentMap =new HashMap<String,Object>();
        if(citedAn.equals("1")){
            patentMap = patentSearchService.searchPatentDetailInfoNoCite(an);
        }else{
            patentMap = patentSearchService.searchPatentDetailInfo(an,citedAn);
        }

        return patentMap;
    }
    /**
     * 根据案卷号 ，到redis中查词
     *
     * @param an      案卷号
     * @param request 用于获取用户id
     * @return 返回一条记录
     */
    @GetMapping("/keyword/search/{an}/{citedAn}")
    public Map<String, List<ZKPatentMark>> searchZKMarkKeywordList(@PathVariable String an,@PathVariable String citedAn, HttpServletRequest request) {
        Map<String, List<ZKPatentMark>> result = new HashMap<String, List<ZKPatentMark>>();
        result.put("patentMarkList", patentSearchService.showMarkList(an));
        result.put("citedPatentMarkList", patentSearchService.showMarkList(citedAn));
        return result;
    }
    /**
     * 保存标引词：一个案卷的词是一个以，分割的字符串
     *
     * @param postMap 单个字符串
     * @param request
     * @return
     */
    @PostMapping("/keyword/save")
    public Map<String, Object> saveZKMarkKeyword(@RequestBody Map postMap, HttpServletRequest request) {
        String an= (String) postMap.get("an");
        Gson gson = new Gson();
        String markStr = (String) postMap.get("markList");
        ArrayList<IpcMark> markList = gson.fromJson(markStr, new TypeToken<List<ZKPatentMark>>(){}.getType());
        int patenttype= (int) postMap.get("patenttype");
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flagPatent;
        flagPatent=patentSearchService.addMark(userId,an,markList,patenttype);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flagPatent);
        return map;
    }
    /**
     * 输入一段文本，返回文本拆词及次数
     */
    @PostMapping("/keyword/searchTextKeyword")
    public Map<String, List<ZKPatentMark>> searchSortByKeywordFreqsList(@RequestBody Map postMap, HttpServletRequest request) {
        Map<String, List<ZKPatentMark>> result = new HashMap<String, List<ZKPatentMark>>();
        String text = (String) postMap.get("text");
        result.put("sortByKeywordFreqsList", patentSearchService.searchSortByKeywordFreqsList(text));
        return result;
    }

    @PostMapping("/keyword/remove/key")
    public Map<String, Object> removeErrorKeyword(@RequestBody Map postMap, HttpServletRequest request) {
        Map result=new HashMap<String,String>();
        String text = (String) postMap.get("errorKeyWord");
        result.put("flag", patentSearchService.removeErrorKeyword(text));
        return result;
    }

    //保存特征检索式到数据库中
    @PostMapping("/searchwords/save")
    public Map<String, Object> saveSearchwords(@RequestBody Map postMap, HttpServletRequest request) {
        String citedAn= (String) postMap.get("citedAn");
        String an = (String) postMap.get("an");
        String searchWords = (String)postMap.get("searchWords");
        String searchWords2 = (String)postMap.get("searchWords2");
        String categoryType = (String)postMap.get("categoryType");
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag= patentSearchService.addSearchWords(an, citedAn,searchWords,categoryType,userId,searchWords2);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }

    //从数据库中查询特征检索式
    @GetMapping("/searchwords/search/{an}/{citedAn}")
    public Map<String, wordmark> searchWordMark(@PathVariable String an, @PathVariable String citedAn, HttpServletRequest request) {
        Map<String,wordmark> result = new HashMap<String,wordmark>();
        wordmark wordmark = patentSearchService.searchWords(an,citedAn);
        result.put("SearchWordMark", wordmark);
        return result;
    }


}
