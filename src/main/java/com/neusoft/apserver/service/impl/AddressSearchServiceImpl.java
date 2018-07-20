package com.neusoft.apserver.service.impl;

import com.neusoft.apserver.dao.AddressRepository;
import com.neusoft.apserver.domain.AddressMark;
import com.neusoft.apserver.service.AddressSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
/**
 * 地址标引-查询模块：service层实现
 * 只有在这一层加事务管理才是真正的事务管理
 * @name fandp
 * @email fandp@neusoft.com
 */

@Service
public class AddressSearchServiceImpl  implements AddressSearchService{
    @Autowired
    private AddressRepository addressRepository;

    /**
     * 查询正在标引的地址
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public List<AddressMark> showMarkingList(String userId) {
        List<AddressMark>  addressMarkingList=addressRepository.findByMarkUser(userId);
        return addressMarkingList;
    }
}
