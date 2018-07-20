package com.neusoft.apserver.dao;

import com.neusoft.apserver.domain.AddressMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 地址模块数据库操作层
 */
public interface AddressRepository extends JpaRepository<AddressMark,String>{

    @Query("select id,an,address,zip from AddressMark where markUser=?1 and marked=2")
    public List<AddressMark> findByMarkUser (String userId);

}
