package com.neusoft.mpserver.dao;
import com.neusoft.mpserver.domain.AddressMarkForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
/**
 * 地址模块数据库操作层--词表
 */
public interface AddressFormRepository  extends JpaRepository<AddressMarkForm,String> {
    //点击保存词，将没有标记的地址退回数据库
    @Modifying
    @Query("update AddressMarkForm   set markUser='', marked='0'  where id  in (?1)")
    public  int updateMarkStatusById(List<String> id);
}
