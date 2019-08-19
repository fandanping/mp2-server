package com.neusoft.mpserver.jszkoffline.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "JSZK_MARK_SEARCHWORDS")
public class wordmark {
    @Id
    @Column(name = "AN")
    private String an;
    @Column(name = "CITEDAN")
    private String citedAn;
    @Column(name = "SEARCHWORDS")
    private String SEARCHWORDS;
    @Column(name = "CATEGORY_TYPE")
    private String categoryType;
    @Column(name = "userId")
    private String userId;

    public wordmark(String  an,String citedAn,String SEARCHWORDS,String categoryType){
        this.an = an;
        this.citedAn = citedAn;
        this.SEARCHWORDS = SEARCHWORDS;
        this.categoryType =categoryType;
    }
}
