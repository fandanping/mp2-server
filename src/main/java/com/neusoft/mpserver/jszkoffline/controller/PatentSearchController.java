package com.neusoft.mpserver.jszkoffline.controller;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.service.PatentSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
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
    public Map<String, Object> searchPatentList( Pagination pagination, String token) {
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

}
