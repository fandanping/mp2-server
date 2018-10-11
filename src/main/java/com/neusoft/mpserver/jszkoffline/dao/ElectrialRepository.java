package com.neusoft.mpserver.jszkoffline.dao;
import com.neusoft.mpserver.jszkoffline.domain.Electrical;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ElectrialRepository  extends JpaRepository<Electrical,String> {

    @Query(value="select count(1) from sipo_markvalidate s where rownum<= 100", nativeQuery = true)
    int findAnCount();

    @Query(value="select s.an,s.P_IPC,s.P_IPC_MAIN,s.pn,s.REFERENCE_CATEGORY,s.cited_an,s.C_IPC from  fantest s order by s.id", nativeQuery = true)
    List<Object[]> findByDetail(Pageable pageable);

    /**
     * 查询对比文献列表：100条，按照id降序，获取前100条记录
     * @param pageable 分页对象
     * @return 返回对比文献列表对象列表
     */
    @Query(value="select s.an,s.P_IPC,s.P_IPC_MAIN,s.pn,s.REFERENCE_CATEGORY,s.cited_an,s.C_IPC from sipo_markvalidate s where rownum<= 100 order by s.id desc", nativeQuery = true)
    List<Object[]> queryCompareFileList(Pageable pageable);

}
