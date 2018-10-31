package com.neusoft.mpserver.jszkoffline.dao;

import com.neusoft.mpserver.jszkoffline.domain.CitedInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CitedInfoRepository extends JpaRepository<CitedInfo,String> {

     @Query(value="select s.an,s.P_IPC_MAIN,s.cited_an,s.REFERENCE_CATEGORY,s.P_IPC,s.C_IPC,s.location  from ( select distinct a.an,a.cited_an,a.p_ipc_main,a.reference_category,a.p_ipc,a.c_ipc,b.location from  uni_abs_patcit_cn_0905  a,jszk_0826 b where   a.an=b.ORIAN and a.CITED_AN=b.COMAN and a.cited_an is not null)  s where s.location>0  and s.REFERENCE_CATEGORY in ('X','Y','PX','PY')and s.P_IPC_MAIN=?1 order by s.location asc,s.an", nativeQuery = true)
     List<Object[]> findByIpcMain(String ipc , Pageable pageable);


       @Query(value="select count(1)  from ( select distinct a.an,a.cited_an,a.pn,a.p_ipc_main,a.reference_category,a.p_ipc,a.c_ipc,b.location,a.CITED_PUB_PN from  uni_abs_patcit_cn_0905  a,jszk_0826 b where   a.an=b.ORIAN and a.CITED_AN=b.COMAN and a.cited_an is not null)  s where s.location>0  and s.REFERENCE_CATEGORY in ('X','Y','PX','PY')and s.P_IPC_MAIN=?1 order by s.location asc", nativeQuery = true)
       int findAnCount(String ipc);


}
