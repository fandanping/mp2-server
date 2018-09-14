package com.neusoft.mpserver.jszkoffline.domain;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Data
@Entity
@Table(name = "jszk_0826")
public class CitedLocation {
    @Id
    private Integer id;
    @Column(name = "BASENUM")
    private String baseNum;
    @Column(name = "COMTYPE")
    private String comType;
    @Column(name = "COMAN")
    private String comAn;
    @Column(name = "LOCATION")
    private String location;
    @Column(name = "ORIAN")
    private String oriAn;

}
