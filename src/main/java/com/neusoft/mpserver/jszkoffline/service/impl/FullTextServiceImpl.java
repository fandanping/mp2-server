package com.neusoft.mpserver.jszkoffline.service.impl;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.jszkoffline.dao.FullTextRepository;
import com.neusoft.mpserver.jszkoffline.domain.FullText;
import com.neusoft.mpserver.jszkoffline.service.FullTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FullTextServiceImpl  implements FullTextService{

    @Autowired
    private FullTextRepository fullTextRepository;

    @Override
    public Map<String, Object> searchFullTextList(Pagination pagination) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<FullText> searchResult = new ArrayList<FullText>();
        //查询total
        int total = fullTextRepository.queryFullTextCount();
        pagination.setTotal(total);
        //获取分页信息，封装Pageable
        int size = pagination.getSize();
        int pageNumber = pagination.getStart() / size;
        Pageable pageable = new PageRequest(pageNumber, size);
        //查询结果集
        searchResult = fullTextRepository.queryFullTextList(pageable);
        resultMap.put("fullTextList", searchResult);
        resultMap.put("pagination", pagination);
        return resultMap;
    }

    @Override
    public Map<String, Object> searchFullTextInfo(String an) {
       return null;
    }
}
