package com.neusoft.mpserver.jszkoffline.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import com.neusoft.mpserver.jszkoffline.service.ZKPatentMarkService;
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
 * trs查询案卷信息：公开日在6月份到九月份 排除外观专利 按照公开日降序排序
 * @name fandp
 * @email fandp@neusoft.com
 */
@RestController
@RequestMapping("/ZKPatentMark")
public class ZKPatentMarkController {
    @Autowired
    private ZKPatentMarkService zkPatentMarkService;
    /**
     * trs查询CNABS库公开日在6月份-九月份 ,排除外观，按照公开日降序，分页查询，返回想要展示的字段信息
     *
     * @param pagination 分页对象
     * @param token      用户标识
     * @return 申请号 标题 公开号 主分类  调用接口标题拆词参考
     */
    @GetMapping("/patent/search/list")
    public Map<String, Object> searchZKPatentList(Pagination pagination, String token) {
        // 如果
      //  Map<String, Object> patentMap = searchZKPatentListByDatasource("TRS",pagination);
        Map<String, Object> patentMap = searchZKPatentListByDatasource("TRS",pagination);
        return patentMap;
    }

    private Map<String, Object> searchZKPatentListByDatasource(String sourceType, Pagination pagination){
        Map<String, Object> patentMap= new HashMap<>();
        if("TRS".equals(sourceType)){
            patentMap = zkPatentMarkService.searchZKPatentList(pagination);
        }else if("Oracle".equals(sourceType)){
           // patentMap = zkPatentMarkService.searchZKPatentListFromOracle(pagination);
        }
        return patentMap;
    }

    /**
     * trs查询权力要求：传入案卷号an,到CNTXT库查询权力要求
     *
     * @param an    案卷申请号
     * @param token 用户标识
     * @return 案卷权力要求 调用接口权利要求拆词参考
     */
    @GetMapping("/patent/search/{an}")
    public Map<String, Object> searchZKPatentDetailInfo(@PathVariable String an, String token) {
        Map<String, Object> patentMap =new HashMap<String,Object>();
        patentMap = zkPatentMarkService.searchZKPatentDetailInfo(an);
        return patentMap;
    }

    //查询标题拆词
    @PostMapping("/chaici/list")
    public Map<String, List> chaiciList (@RequestBody Map postMap){
        String ti = (String) postMap.get("title");
        Map<String, List> result = new HashMap<String, List>();
        result.put("ZKchaiCiList", zkPatentMarkService.showChaiCiList(ti));
        return result;
    }

    /**
     * 根据案卷号 ，到redis中查词
     *
     * @param an      案卷号
     * @param request 用于获取用户id
     * @return 返回一条记录
     */
   @GetMapping("/keyword/search/{an}")
    public Map<String, List<ZKPatentMark>> searchZKMarkKeywordList(@PathVariable String an, HttpServletRequest request) {
       Map<String, List<ZKPatentMark>> result = new HashMap<String, List<ZKPatentMark>>();
       List<ZKPatentMark> list = getZKPatentMarkList(an, "Redis");
       result.put("zkmarkList", list);
       return result;
    }

    private List<ZKPatentMark> getZKPatentMarkList(String an, String resouceType){
        if("Redis".equals(resouceType))
            return zkPatentMarkService.showMarkListFromRedis(an);
        else
            return zkPatentMarkService.showMarkList(an);
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
        Gson gson = new Gson();
        String markStr = (String) postMap.get("markList");
        ArrayList<IpcMark> markList = gson.fromJson(markStr, new TypeToken<List<ZKPatentMark>>(){}.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        int patenttype= (int) postMap.get("patenttype");
        String an = (String) postMap.get("an");
        boolean flag= saveZKMark(userId, an,markList,patenttype,"Redis");
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }

    private boolean saveZKMark(String userId,String an,List markList,int patenttype,String targetResource){
        boolean flag = false;
        if("Redis".equals(targetResource)){
            flag = zkPatentMarkService.addZKMarkToRedis(userId,an,markList,patenttype);
        }else{
            flag = zkPatentMarkService.addZKMark(userId,an,markList,patenttype);
        }
        return flag;
    }

    @PostMapping("/keyword/remove/key")
    public Map<String, Object> removeErrorKeyword(@RequestBody Map postMap, HttpServletRequest request) {
        Map result=new HashMap<String,String>();
        String text = (String) postMap.get("errorKeyWord");
        result.put("removeZKErrorKeyWordFlag", zkPatentMarkService.removeErrorKeyword(text));
        return result;
    }

    /**
     * 删除标引词
     *
     * @param postMap
     * @param request
     * @return
     */
   /* @PostMapping("/keyword/delete")
    public Map<String, Object> deleteZKMarkKeyword(@RequestBody Map postMap, HttpServletRequest request) {


    }*/
}