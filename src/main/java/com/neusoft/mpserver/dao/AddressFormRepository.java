package com.neusoft.mpserver.dao;

import com.neusoft.mpserver.domain.AddressMark;
import com.neusoft.mpserver.domain.AddressMarkForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressFormRepository  extends JpaRepository<AddressMarkForm,String> {
    //点击保存词，将没有标记的地址退回数据库
    @Modifying
    @Query("update AddressMarkForm   set markUser='', marked=''  where id=?1 and  marked='2'")
    public  int updateMarkStatusById(List<String> id);
}
