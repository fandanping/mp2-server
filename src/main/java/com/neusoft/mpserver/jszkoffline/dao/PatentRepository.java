package com.neusoft.mpserver.jszkoffline.dao;

import com.neusoft.mpserver.jszkoffline.domain.Patent;
import com.neusoft.mpserver.jszkoffline.domain.wordmark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;
import java.util.List;

public interface PatentRepository extends JpaRepository<Patent,String> {

  /*  @Query(value="  select s.an, s.P_IPC_MAIN, s.NRD_AN,s.REFERENCE_CATEGORY,s.P_IPC,s.C_IPC  from (select ROWNUM rn, t.an, t.P_IPC_MAIN, t.NRD_AN,t.REFERENCE_CATEGORY,t.P_IPC,t.C_IPC from uni_abs_patcit_cn_1027 t where t.p_inv_title is not null and t.c_inv_title is not null  and t.reference_category in ('X', 'Y') and t. p_inv_title like '%电机%' and rownum <= 200 order by t.p_ipc desc) s where rn > 100", nativeQuery = true)
    List<Object[]> findPatentList( Pageable pageable);


    @Query(value="  select count(1) from (select ROWNUM rn, t.p_inv_title, t.c_inv_title, t.reference_category from uni_abs_patcit_cn_1027 t where t.p_inv_title is not null and t.c_inv_title is not null  and t.reference_category in ('X', 'Y')  and  t. p_inv_title like '%电机%' and rownum <= 200 order by t.p_ipc desc) s where rn > 100", nativeQuery = true)
    int findAnCount();
*/
  //@Query(value="select s.an,s.P_IPC_MAIN,s.cited_an,s.REFERENCE_CATEGORY,s.P_IPC,s.C_IPC,s.location  from ( select distinct a.an,a.cited_an,a.p_ipc_main,a.reference_category,a.p_ipc,a.c_ipc,b.location from  uni_abs_patcit_cn_0905  a,jszk_0826 b where   a.an=b.ORIAN and a.CITED_AN=b.COMAN and a.cited_an is not null)  s where s.location>0  and s.REFERENCE_CATEGORY in ('X','Y','PX','PY')and s.P_IPC_MAIN='H01B7/17' order by s.location asc,s.an", nativeQuery = true)
  //List<Object[]> findPatentList( Pageable pageable);
//C08L71/12   G01N33/00 H01R4/18 H01T13/39 H01B7/17 G02B21/00
  //  @Query(value="select count(1)  from ( select distinct a.an,a.cited_an,a.pn,a.p_ipc_main,a.reference_category,a.p_ipc,a.c_ipc,b.location,a.CITED_PUB_PN from  uni_abs_patcit_cn_0905  a,jszk_0826 b where   a.an=b.ORIAN and a.CITED_AN=b.COMAN and a.cited_an is not null)  s where s.location>0  and s.REFERENCE_CATEGORY in ('X','Y','PX','PY')and s.P_IPC_MAIN='H01B7/17' order by s.location asc", nativeQuery = true)
   // int findAnCount();
    @Query(value="select s.an,'000000' P_IPC_MAIN,s.cited_an,s.REFERENCE_CATEGORY,'000000' P_IPC,'000000' C_IPC,'000000' location  from uni_abs_patcit_cn_new s  where s.reference_category in ('X','Y')", nativeQuery = true)
    List<Object[]> findPatentList( Pageable pageable);
//C08L71/12   G01N33/00 H01R4/18 H01T13/39 H01B7/17 G02B21/00
    @Query(value="select count(1)  from  uni_abs_patcit_cn_new s where s.REFERENCE_CATEGORY in('X','Y') order by s.an", nativeQuery = true)
    int findAnCount();

    //保存标引词 一对一
    @Transactional
    @Modifying
    @Query(value = "insert into jszk_mark_dic_errorkey(key_word) values (?)", nativeQuery = true)
    public int saveErrorKeyWord(String erroKeyword);

    // 2019 06 11x修改
    @Query(value="select s.an,s.ic,s.cited_an,s.REFERENCE_CATEGORY,s.CITED_IC,'0000' location  from mark_demo_01 s  where s.reference_category='X' and s.ti like '%电解液%'", nativeQuery = true)
    List<Object[]> findPatentListNew( Pageable pageable);
    //C08L71/12   G01N33/00 H01R4/18 H01T13/39 H01B7/17 G02B21/00
    @Query(value="select count(1)  from  mark_demo_01 s where s.REFERENCE_CATEGORY='X' and s.ti like '%电解液%' order by s.an", nativeQuery = true)
    int findAnCountNew();



}
