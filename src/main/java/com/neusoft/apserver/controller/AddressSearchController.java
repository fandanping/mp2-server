package com.neusoft.apserver.controller;

import com.neusoft.apserver.domain.AddressMark;
import com.neusoft.apserver.domain.Constant;
import com.neusoft.apserver.service.AddressSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
