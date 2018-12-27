package com.neusoft.mpserver.jszkoffline.dao;

import com.neusoft.mpserver.jszkoffline.domain.ElectrialTiMark;
import com.neusoft.mpserver.jszkoffline.domain.Patent;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatent;
import com.neusoft.mpserver.jszkoffline.domain.ZKPatentMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ZKPatentRepository extends JpaRepository<ZKPatent,String> {
    //保存错误词 纠正词典
    @Transactional
    @Modifying
    @Query(value = "insert into jszk_mark_dic_errorkey(key_word) values (?)", nativeQuery = true)
    public int saveErrorKeyWord(String erroKeyword);

    //保存标引词
    @Transactional
    @Modifying
    @Query(value = "insert into jszk_mark_dic(an, tiwords,otherwords,invtype) values (?,?,?,?)", nativeQuery = true)
    public int saveKeyWord(String an,String tiwords,String otherswords,int patenttype);

    //查询标引词
    @Query(value = "select an,nvl(tiwords,' '),nvl(otherwords,' '),nvl(invtype,1) from jszk_mark_dic where an=?1",nativeQuery = true)
    public List<Object[]> findZKMarksByAn(String an);


   //更新标引词
   @Transactional
   @Modifying
   @Query(value = "update jszk_mark_dic set tiwords=?2 ,otherwords=?3 ,invtype=?4  where an=?1", nativeQuery = true)
   public int updateZKMark(String an,String tiwords,String otherswords,int patenttype);


}
