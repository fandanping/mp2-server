package com.neusoft.mpserver.jszkoffline.dao;
import com.neusoft.mpserver.jszkoffline.domain.ElectrialTiMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;
import java.util.List;

public interface ElectrialMarkRepository  extends JpaRepository<ElectrialTiMark,String> {
    //查询标引词
    @Query(value = "select *  from SIPO_MP_TI_MARK where an=?1 and cited_an=?2", nativeQuery = true)
    public List<ElectrialTiMark> findByAnAAndCitedAn(String an,String citedAn);

    //删除标引词
    @Transactional
    @Modifying
    @Query(value = "delete from ElectrialTiMark where id=?1 and userId=?2")
    public void deleteTiMarkByIdAndUserId(String id, String userId);

    //保存标引词 一对一
    @Transactional
    @Modifying
    @Query(value = "update ElectrialTiMark set word=?3 where an=?1 and citedAn=?2")
    public int saveMark(String an,String citedAn, String word);

}
