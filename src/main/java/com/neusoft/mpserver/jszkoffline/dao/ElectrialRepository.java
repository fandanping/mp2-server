package com.neusoft.mpserver.jszkoffline.dao;
import com.neusoft.mpserver.jszkoffline.domain.Electrical;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ElectrialRepository  extends JpaRepository<Electrical,String> {

    @Query(value="select count(1) from fantest", nativeQuery = true)
    int findAnCount();

    @Query(value="select s.an,s.P_IPC,s.P_IPC_MAIN,s.pn,s.REFERENCE_CATEGORY,s.cited_an,s.C_IPC from  fantest s order by s.id", nativeQuery = true)
    List<Object[]> findByDetail(Pageable pageable);

}
