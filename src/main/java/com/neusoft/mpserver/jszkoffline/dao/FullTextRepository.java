package com.neusoft.mpserver.jszkoffline.dao;

import com.neusoft.mpserver.jszkoffline.domain.FullText;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FullTextRepository  extends JpaRepository<FullText,String> {

    @Query(value="select new com.neusoft.mpserver.jszkoffline.domain.FullText(ap,clms_first_one,background_last_one,desc_first_one) from FullText")
    List<FullText> queryFullTextList(Pageable pageable);
    @Query(value="select count(1) from FullText ")
    int queryFullTextCount();
}
