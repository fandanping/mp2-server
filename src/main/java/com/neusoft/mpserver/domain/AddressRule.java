package com.neusoft.mpserver.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "SIPO_AP_ADDRESS_RULE")
public class AddressRule {
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
    @Column(insertable = false, updatable = false)
    private String userId;

    private String address;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "userId")
    private User user = new User();

/*    public AddressRule(String id, String province, String city, String area,Date createTime,String rule,String userId,String address) {
        this.id = id;
        this.province = province;
        this.city = city;
        this.area = area;
        this.createTime = createTime;
        this.userId = userId;
        this.address = address;
    }*/

}
