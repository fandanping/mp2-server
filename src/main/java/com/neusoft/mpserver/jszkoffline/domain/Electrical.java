package com.neusoft.mpserver.jszkoffline.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "sipo_markvalidate")
public class Electrical {

    @Id
    private Integer id;
    //申请号
    private String an;
    //公开号
    private String pn;
    //P_IPC
    @Column(name = "P_IPC")
    private String pIpc;
    //主分类号
    @Column(name = "P_IPC_MAIN")
    private String ipcMain;
    //引用文献申请号
    @Column(name = "CITED_AN")
    private String citedAn;
    //引用文献公开号
    @Column(name = "CITED_PUB_PN")
    private String citedPn;
    //引用类型
    @Column(name = "REFERENCE_CATEGORY")
    private String referenceType;
    //CITED_PUB_CN
    // @Column(name = "CITED_PUB_CN")
    // private String citedPubCn;
    //CITED_PUB_SN
    // private String citedPubSn;
    //C_IPC
    @Column(name = "C_IPC")
    private String  cIpc;
    //C_IPC_MAIN
    //private String cIpcMain;

}
