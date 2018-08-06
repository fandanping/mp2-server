package com.neusoft.mpserver.dao;

import com.neusoft.mpserver.domain.AddressRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * 规则： 数据操作层
 */
public interface AddressRuleRepository extends JpaRepository<AddressRule, String> {

    //查询今天所有的规则，不带分页

    @Query(value = "select rule_id,nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date(?1,'yyyy/mm/dd hh24:mi:ss') order by create_time desc", nativeQuery = true)
    public List<Object[]> queryRule(String time);

    //查询今天所有的规则，带分页
    //1.查询所有的，带关键词
    @Query(value = "select rule_id,nvl(address,''), nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and rule like ?2 order by create_time desc", nativeQuery = true)
    List<Object[]> findRuleAllBykey(String time, String keyword, Pageable pageable);

    //查询所有的，不带关键词
    @Query(value = "select rule_id,nvl(address,''), nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') order by create_time desc", nativeQuery = true)
    List<Object[]> findRuleAll(String time, Pageable pageable);

    //查询自己的，带关键词
    @Query(value = "select rule_id,nvl(address,''), nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and rule like ?2 and user_id=?3  order by create_time desc", nativeQuery = true)
    List<Object[]> findRuleMeBykey(String time, String keyword, String userId, Pageable pageable);

    //查询自己的，不带关键词
    @Query(value = "select rule_id,nvl(address,''), nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss')  and user_id=?2 order by create_time desc", nativeQuery = true)
    List<Object[]> findRuleMe(String time, String userId, Pageable pageable);

    //查询他人的，带关键词
    @Query(value = "select rule_id,nvl(address,''), nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and rule like ?2 and user_id !=?3 order by create_time desc ", nativeQuery = true)
    List<Object[]> findRuleOtherBykey(String time, String keyword, String userId, Pageable pageable);

    //查询他人的，不带关键词
    @Query(value = "select rule_id,nvl(address,''), nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss')  and user_id != ?2 order by create_time desc", nativeQuery = true)
    List<Object[]> findRuleOther(String time, String userId, Pageable pageable);

    //修改规则
    @Modifying
    @Query("update AddressRule set rule=?6,province=?3,city=?4,area=?5 where id=?2 and userId=?1")
    public int updateRule(String userId, String id, String province, String city, String area, String rule);

    //查询今天所有的总数
    @Query(value = "select count(1) from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss')", nativeQuery = true)
    int findRuleAllCount(String time);

    //查询自己今天设置的总数
    @Query(value = "select count(1) from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and user_id=?2", nativeQuery = true)
    int findRuleMeCount(String time,String userId);
    //查询其他人今天设置的总数
    @Query(value = "select count(1) from sipo_ap_address_rule where create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and user_id !=?2", nativeQuery = true)
    int findRuleOtherCount(String time,String userId);


}
