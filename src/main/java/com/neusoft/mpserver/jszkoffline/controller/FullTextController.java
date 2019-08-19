package com.neusoft.mpserver.jszkoffline.controller;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.service.FullTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/fulltext")
public class FullTextController {

    @Autowired
    private FullTextService fullTextService;

    //查询案卷申请号列表
    @GetMapping("/search/list")
    public Map<String, Object> searchFullTextList(Pagination pagination, String token) {
        Map<String, Object> fullTextMap = fullTextService.searchFullTextList(pagination);
        return fullTextMap;
    }
}
