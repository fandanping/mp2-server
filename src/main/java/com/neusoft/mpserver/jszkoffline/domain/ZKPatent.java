package com.neusoft.mpserver.jszkoffline.domain;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="jszk_mark_dic")
public class ZKPatent {
    @Id
    private String an;
    //标引词
    private String tiwords;

    private String otherwords;

    private int  invtype = 1;
}
