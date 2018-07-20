package com.neusoft.apserver.service;

import com.neusoft.apserver.domain.AddressMark;
import com.neusoft.apserver.domain.IpcMark;

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

}
