package com.neusoft.apserver.controller;

import com.neusoft.apserver.common.domain.Pagination;
import com.neusoft.apserver.service.IpcSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * ipc:查询ic下案卷模块
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@RestController
@RequestMapping("/ipc")
public class IpcSearchController {
    @Autowired
    private IpcSearchService searchService;

    //查询案卷列表
    @GetMapping("/search/list/{ipc}")
    public Map<String, Object> searchPatentList(@PathVariable String ipc, Pagination pagination, String token) {
        Map<String, Object> patentMap = searchService.searchPatentList(ipc.replaceAll("-", "/"), pagination);
        return patentMap;
    }

    //查询中英文解释
    @GetMapping("/search/{ipc}")
    public Map<String, Map<String, String>> searchIpc(@PathVariable String ipc, String token) {
        Map<String, String> ipcResult = searchService.searchIpc(ipc.replaceAll("-", "/"));
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        result.put("ipcResult", ipcResult);
        return result;
    }


}
