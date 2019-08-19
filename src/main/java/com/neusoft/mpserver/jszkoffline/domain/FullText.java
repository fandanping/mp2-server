package com.neusoft.mpserver.jszkoffline.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Table(name ="JSZK_DESC_TXT")
public class FullText {
    @Id
    private String  ap;
    @Column(name = "CLMS")
    private String clms_first_one;
    @Column(name = "BACKGROUND")
    private String background_last_one;
    @Column(name = "DISCLOSURE")
    private String desc_first_one;

    public FullText(String  ap,String clms_first_one,String background_last_one,String desc_first_one){
         this.ap = ap;
         this.background_last_one = background_last_one;
         this.clms_first_one = clms_first_one;
         this.desc_first_one = desc_first_one;
    }
}
