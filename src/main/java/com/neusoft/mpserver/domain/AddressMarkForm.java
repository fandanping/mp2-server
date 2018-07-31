package com.neusoft.mpserver.domain;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "SIPO_AP_ADDRESS")
public class AddressMarkForm {
    //用作标词id
    @Id
    @Column(name ="qid")
    private String id;
    //省
    private String province;
    //市
    private String city;
    //区、县
    private String area;
    //镇
    private String town;
    //是否已标记marked
    private String marked;
    //标记人
    private String markUser;
    //标记时间
    private String markTime;
    //状态
    private Integer status;
    //address
    private String address;
}
