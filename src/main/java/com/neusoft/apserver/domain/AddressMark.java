package com.neusoft.apserver.domain;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * address映射实体类
 * @name fandp
 * @email fandp@neusoft.com
 */
@Data
@Entity
@Table(name = "SIPO_AP_ADDRESS_TEST")
public class AddressMark {
    //ID
    @Column(name ="id")
    private Integer numberId;
    //案卷号an
    private String an;
    //发明类别
    private String inventType;
    //公开日期
    private String pubDate;
    //申请人姓名
    private String  appDame;
    //申请人所在国
    private String  appCountry;
    //申请人地址APP_ADDRESS
    @Column(name="app_address")
    private String address;
    //申请人所在邮编APP_ZIP
    @Column(name="app_zip")
    private String zip;
    //用作标词id
    @Id
    @Column(name ="qid")
    private String id;
    //省
    private String provice;
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


}
