package com.neusoft.mpserver.jszkoffline.dao;

import com.neusoft.mpserver.jszkoffline.domain.Patent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatentRepository extends JpaRepository<Patent,String> {

    @Query(value="  select s.an, s.P_IPC_MAIN, s.NRD_AN,s.REFERENCE_CATEGORY,s.P_IPC,s.C_IPC  from (select ROWNUM rn, t.an, t.P_IPC_MAIN, t.NRD_AN,t.REFERENCE_CATEGORY,t.P_IPC,t.C_IPC from uni_abs_patcit_cn_1027 t where t.p_inv_title is not null and t.c_inv_title is not null  and t.reference_category in ('X', 'Y') and t. p_inv_title like '%电机%' and rownum <= 200 order by t.p_ipc desc) s where rn > 100", nativeQuery = true)
    List<Object[]> findPatentList( Pageable pageable);


    @Query(value="  select count(1) from (select ROWNUM rn, t.p_inv_title, t.c_inv_title, t.reference_category from uni_abs_patcit_cn_1027 t where t.p_inv_title is not null and t.c_inv_title is not null  and t.reference_category in ('X', 'Y')  and  t. p_inv_title like '%电机%' and rownum <= 200 order by t.p_ipc desc) s where rn > 100", nativeQuery = true)
    int findAnCount();


}
