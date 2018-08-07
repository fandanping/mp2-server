package com.neusoft.mpserver.dao;

import com.neusoft.mpserver.domain.IpcMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * mark操作数据库层
 *
 * @Query注解查询适用于所查询的数据无法通过关键字查询得到结果的查询。这种查询可以摆脱像关键字查询那样的约束，将查询直接在相应的接口方法中声明，结构更为清晰，这是Spring Data的特有实现。
 * 　1、索引参数如下所示，索引值从1开始，查询中"?X"个数需要与方法定义的参数个数相一致，并且顺序也要一致。
 * 例如:@Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2")
 * 2. 命名参数(推荐使用此方式)：可以定义好参数名，赋值时使用@Param("参数名"),而不用管顺序。
 * 例如：@Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.email = :email")
 * List<Person> testQueryAnnotationParams2(@Param("email") String email, @Param("lastName") String lastName);
 * @name fandp
 * @email fandp@neusoft.com
 */
public interface IpcMarkRepository extends JpaRepository<IpcMark, String> {

    public List<IpcMark> findByAn(String an);

    //自定义sql查询
    @Modifying
    @Query(value = "delete from IpcMark where id=?1 and userId=?2")
    public void deleteMarkByIdAndUserId(String id, String userId);

    @Query(value = "select distinct(an) from IpcMark where an in (?1)")
    public List matchMarkByAn(List<String> AList);
}
