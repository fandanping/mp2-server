package com.neusoft.mpserver.jszkoffline.dao;
import com.neusoft.mpserver.jszkoffline.domain.ElectrialTiMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import javax.transaction.Transactional;
import java.util.List;

public interface ElectrialMarkRepository  extends JpaRepository<ElectrialTiMark,String> {
    //查询标引词
    public List<ElectrialTiMark> findTIMarkByAn(String an);

    //删除标引词
    @Transactional
    @Modifying
    @Query(value = "delete from ElectrialTiMark where id=?1 and userId=?2")
    public void deleteTiMarkByIdAndUserId(String id, String userId);

    //保存标引词 一对一
    @Transactional
    @Modifying
    @Query(value = "update ElectrialTiMark set word=?2 where an=?1")
    public int saveMark(String an, String word);

}
