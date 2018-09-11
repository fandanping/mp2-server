package com.neusoft.mpserver.sipo57.dao;

import com.neusoft.mpserver.sipo57.domain.AddressRule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 规则： 数据操作层
 */
public interface AddressRuleRepository extends JpaRepository<AddressRule, String> {

    //查询今天所有的规则，不带分页

    //@Query(value = "select rule_id,nvl(rule,''),nvl(province,''),nvl(city,''),nvl(area,''),nvl(user_id,'') from sipo_ap_address_rule where create_time >to_date(?1,'yyyy/mm/dd hh24:mi:ss') order by create_time desc", nativeQuery = true)
    @Query(value = "select a.rule_id,nvl(a.rule,''),nvl(a.province,''),nvl(a.city,''),nvl(a.area,''),nvl(a.user_id,''),b.username from sipo_ap_address_rule a, sipo_mp_user b where a.create_time >to_date(?1,'yyyy/mm/dd hh24:mi:ss')  and a.user_id=b.id order by a.create_time desc", nativeQuery = true)
    public List<Object[]> queryRule(String time);

    //查询今天所有的规则，带分页
    //1.查询所有的，带关键词
    @Query(value = "select a.rule_id,nvl(a.address,''), nvl(a.rule,''),nvl(a.province,''),nvl(a.city,''),nvl(a.area,''),nvl(a.user_id,''),b.username  from sipo_ap_address_rule a ,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.rule like ?2  and a.user_id=b.id order by a.create_time desc", nativeQuery = true)
    List<Object[]> findRuleAllBykey(String time, String keyword, Pageable pageable);

    //查询所有的，不带关键词
    @Query(value = "select a.rule_id,nvl(a.address,''), nvl(a.rule,''),nvl(a.province,''),nvl(a.city,''),nvl(a.area,''),nvl(a.user_id,'') ,b.username from sipo_ap_address_rule a,sipo_mp_user b  where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss')  and a.user_id=b.id order by a.create_time desc", nativeQuery = true)
    List<Object[]> findRuleAll(String time, Pageable pageable);

    //查询自己的，带关键词
    @Query(value = "select a.rule_id,nvl(a.address,''), nvl(a.rule,''),nvl(a.province,''),nvl(a.city,''),nvl(a.area,''),nvl(a.user_id,''),b.username from sipo_ap_address_rule a,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.rule like ?2 and a.user_id=?3  and a.user_id=b.id order by a.create_time desc", nativeQuery = true)
    List<Object[]> findRuleMeBykey(String time, String keyword, String userId, Pageable pageable);

    //查询自己的，不带关键词
    @Query(value = "select a.rule_id,nvl(a.address,''), nvl(a.rule,''),nvl(a.province,''),nvl(a.city,''),nvl(a.area,''),nvl(a.user_id,''),b.username from sipo_ap_address_rule a,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss')  and a.user_id=?2  and a.user_id=b.id order by a.create_time desc", nativeQuery = true)
    List<Object[]> findRuleMe(String time, String userId, Pageable pageable);

    //查询他人的，带关键词
    @Query(value = "select a.rule_id,nvl(a.address,''), nvl(a.rule,''),nvl(a.province,''),nvl(a.city,''),nvl(a.area,''),nvl(a.user_id,'') ,b.username from sipo_ap_address_rule a,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.rule like ?2 and a.user_id !=?3 and a.user_id=b.id order by a.create_time desc ", nativeQuery = true)
    List<Object[]> findRuleOtherBykey(String time, String keyword, String userId, Pageable pageable);

    //查询他人的，不带关键词
    @Query(value = "select a.rule_id,nvl(a.address,''), nvl(a.rule,''),nvl(a.province,''),nvl(a.city,''),nvl(a.area,''),nvl(a.user_id,'') ,b.username  from sipo_ap_address_rule a,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss')  and a.user_id != ?2 and a.user_id=b.id order by a.create_time desc", nativeQuery = true)
    List<Object[]> findRuleOther(String time, String userId, Pageable pageable);

    //修改规则
    @Modifying
    @Query("update AddressRule set rule=?6,province=?3,city=?4,area=?5 where id=?2 and userId=?1")
    public int updateRule(String userId, String id, String province, String city, String area, String rule);

    //查询今天所有的总数 不带关键词
    @Query(value = "select count(1) from sipo_ap_address_rule a ,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.user_id=b.id", nativeQuery = true)
    int findRuleAllCount(String time);
    //查询今天所有的总数 带关键词
    @Query(value = "select count(1) from sipo_ap_address_rule a ,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.user_id=b.id and a.rule like ?2", nativeQuery = true)
    int findRuleAllCountBykey(String time, String keyword);

    //查询自己今天设置的总数 不带关键词
    @Query(value = "select count(1) from sipo_ap_address_rule a,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.user_id=?2 and a.user_id=b.id", nativeQuery = true)
    int findRuleMeCount(String time, String userId);
    //查询自己今天设置的总数 带关键词
    @Query(value = "select count(1) from sipo_ap_address_rule a,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.user_id=?2 and a.user_id=b.id and a.rule like ?3", nativeQuery = true)
    int findRuleMeCountBykey(String time, String userId, String keyword);

    //查询其他人今天设置的总数a
    @Query(value = "select count(1) from sipo_ap_address_rule a,sipo_mp_user b  where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.user_id !=?2 and a.user_id=b.id", nativeQuery = true)
    int findRuleOtherCount(String time, String userId);
    //查询其他人今天设置的总数a 带关键词
    @Query(value = "select count(1) from sipo_ap_address_rule a,sipo_mp_user b  where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.user_id !=?2 and a.user_id=b.id and a.rule like ?3", nativeQuery = true)
    int findRuleOtherCountBykey(String time, String userId, String keyword);

    //删除标引规则
    @Modifying
    @Query(value = "delete from AddressRule where id=?1 and userId=?2")
    void deleteByIdAndUserId(String id, String userId);

}
