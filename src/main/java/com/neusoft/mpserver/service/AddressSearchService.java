package com.neusoft.mpserver.service;

import com.neusoft.mpserver.domain.AddressMark;
import java.util.List;

/**
 * 地址标引模块：service层接口
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
public interface AddressSearchService {
    //查询正在标引列表
    public List<AddressMark> showMarkingList(String userId);
    //查询未标引地址列表
    public List<AddressMark> showUnMarkList(String userId,String keyword);
    //保存标引词
    public boolean addMark(String userId, List<AddressMark> markList);
}
