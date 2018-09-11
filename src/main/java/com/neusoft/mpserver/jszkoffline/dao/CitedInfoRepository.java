package com.neusoft.mpserver.jszkoffline.dao;

import com.neusoft.mpserver.jszkoffline.domain.CitedInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CitedInfoRepository extends JpaRepository<CitedInfo,String> {
       @Query(value="select id,an,pn,ipcMain,citedAn,citedPn,referenceType,pIpc,cIpc from  CitedInfo where ipcMain=?1 order by id desc")
       List<Object[]> findByIpcMain(String ipc , Pageable pageable);
       @Query(value="select count(1) from  uni_abs_patcit_cn_0905  where P_IPC_MAIN=?1", nativeQuery = true)
       int findAnCount(String ipc);

      /* @Query(value = "select count(1) from sipo_ap_address_rule a ,sipo_mp_user b where a.create_time >to_date( ?1, 'yyyy/mm/dd hh24:mi:ss') and a.user_id=b.id", nativeQuery = true)
       int findRuleAllCount(String time);*/

}
