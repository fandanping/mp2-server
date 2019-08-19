package com.neusoft.mpserver.jszkoffline.dao;

import com.neusoft.mpserver.jszkoffline.domain.wordmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface WordMarkRepository  extends JpaRepository<wordmark,String> {
    //保存特征检索式
    @Transactional
    @Modifying
    @Query(value = "insert into JSZK_MARK_SEARCHWORDS(an, citedAn,SEARCHWORDS,category_Type,userId) values (?,?,?,?,?)", nativeQuery = true)
    public int saveSearchWords(String an,String citedAn,String words,String categoryType,String userId);

    //更新特征检索式
    @Transactional
    @Modifying
    @Query(value = "update JSZK_MARK_SEARCHWORDS set SEARCHWORDS=?3, category_Type=?4 ,userId=?5 where an=?1 and citedan=?2", nativeQuery = true)
    public int updateSearchWords(String an,String citedAn,String words,String categoryType,String userId);

    //查询特征检索式
    @Query(value = "select new com.neusoft.mpserver.jszkoffline.domain.wordmark(an,citedAn,SEARCHWORDS,categoryType) from wordmark where an=?1 and citedan=?2")
    public wordmark findSearchWords(String an, String citedAn);
}
