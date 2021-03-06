package com.neusoft.mpserver.sipo57.dao;

import com.neusoft.mpserver.sipo57.domain.AddressMarkForm;
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

    //精准去重 app_name address
    @Modifying
    @Query("update AddressMarkForm set province=?2,city=?3,area=?4,town=?5 ,markTime=?6,status=?7,markUser=?1,marked='1' where address=?9 and appName=?8")
    public int updateSameAddress(String userId, String province, String city, String area, String town, String date, Integer status, String appName, String address);

    //暂不标引
    @Modifying
    @Query("update AddressMarkForm set marked='4' where address=?2 and appName=?1")
    public int updateNoMarkAddress(String appName, String address);

    //已有规则，等待更新
    @Modifying
    @Query("update AddressMarkForm set marked='5' where id=?1")
    public int waitMarkAddress(String id);
}
