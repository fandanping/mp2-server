package com.neusoft.mpserver.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "SIPO_AP_ADDRESS_RULE")
public class AddressRuleForm {
    //用作标词id
    @Id
    @Column(name = "rule_id")
    private String id;
    //省
    private String province;
    //市
    private String city;
    //区、县
    private String area;

    private Date createTime;

    private String rule;

    private String userId;

    private String address;

}
