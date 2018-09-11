package com.neusoft.mpserver.sipo57.dao;

import com.neusoft.mpserver.sipo57.domain.AddressMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 地址模块数据库操作层--地址表
 */
public interface AddressRepository extends JpaRepository<AddressMark,String>{
    //查询之前未标记的地址
    @Query("select id,an,address,zip,appName from AddressMark where markUser=?1 and marked=2")
    public List<Object[]> findByMarkUser(String userId);

    //将这个用户下的之前未标记的地址退回数据库
    @Modifying
    @Query("update AddressMark   set markUser='', marked='0'  where markUser=?1 and  marked='2'")
    public  int updateMarkStatus(String userId);

    //随机top20
   // @Query(value="select id, an, address, zip from (select nvl(qid,' ') as id, nvl(an, ' ') as an,  nvl(app_address, ' ') as address, nvl(app_zip, ' ') as zip from sipo_ap_address_test where marked is null order by dbms_random.value) where rownum <=20", nativeQuery=true)
   // @Query(value="select nvl(qid,' ') ,nvl(an, ' '),nvl(app_address, ' ') ,nvl(app_zip, ' ') from sipo_ap_address_test where marked is null  and rownum <=20", nativeQuery=true)
    //@Query(value="select id ,an,address ,zip from AddressMark where marked is null  and rownum <=20")
    @Query(value="select /*+parallel(t,32)*/ nvl(t.qid,' ') ,nvl(t.an, ' '),nvl(t.app_address, ' ') ,nvl(t.app_zip, ' ') ,nvl(t.app_name,' ') from sipo_ap_address t where t.marked='0'  and rownum <=20", nativeQuery=true)
    public List<Object[]> findByRandom();

    //模糊查询，随机top20
  // @Query(value="select id, an, address, zip from (select nvl(qid,' ') as id, nvl(an, ' ') as an,  nvl(app_address, ' ') as address, nvl(app_zip, ' ') as zip from sipo_ap_address_test where marked is null and app_address like '%'||?1||'%' order by dbms_random.value) where rownum <=20", nativeQuery=true)
    @Query(value="select /*+parallel(t,32)*/ nvl(t.qid,' ') ,nvl(t.an, ' '),nvl(t.app_address, ' ') ,nvl(t.app_zip, ' '),nvl(t.app_name,' ') from sipo_ap_address t where t.marked='0' and t.app_address like ?1 and  rownum <=20", nativeQuery=true)
    public List<Object[]> findByMarkedAndAddressLike(String keyword);

    //将这条地址数据归为这个人所有，标记：正在标记
    @Modifying
    @Query("update AddressMark  set markUser=?2, marked='2'  where id in (?1)")
    public  int updateMarkUser(List<String> id, String userId);

}
