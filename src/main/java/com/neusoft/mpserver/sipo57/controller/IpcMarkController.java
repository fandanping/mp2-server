package com.neusoft.mpserver.sipo57.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neusoft.mpserver.sipo57.domain.Constant;
import com.neusoft.mpserver.sipo57.domain.IpcMark;
import com.neusoft.mpserver.sipo57.service.IpcMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  ipc:标引模块
 * @name fandp
 * @email fandp@neusoft.com
 */
@RestController
@RequestMapping("/ipc")
public class IpcMarkController {
    @Autowired
    private IpcMarkService markService;

    //保存标引词
    @PostMapping("/add")
    public  Map<String ,Object> addMark(@RequestBody Map postMap, HttpServletRequest request){
        Gson gson = new Gson();
        String markStr = (String) postMap.get("markList");
        ArrayList<IpcMark> markList = gson.fromJson(markStr, new TypeToken<List<IpcMark>>(){}.getType());
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag=markService.addMark(userId,markList);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }

    //删除标引词
    @PostMapping("/delete")
    public Map<String,Object>  deleteMark(@RequestBody Map postMap,HttpServletRequest request){
        String markId = (String) postMap.get("markId");
        String userId = (String) request.getAttribute(Constant.USER_ID);
        boolean flag= markService.deleteMark(markId,userId);
        Map<String ,Object> map=new HashMap<String,Object>();
        map.put("flag",flag);
        return map;
    }

    //查询显示标引词
    @GetMapping("/list/{an}")
     public Map<String, List<IpcMark>> markList (@PathVariable String an){
        Map<String, List<IpcMark>> result = new HashMap<String, List<IpcMark>>();
        result.put("markList", markService.showMarkList(an));
          return result;
     }

}
