package com.neusoft.mpserver.jszkoffline.domain;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "UNI_ABS_PATCIT_CN_1027")
public class Patent {

   // private Integer id;
    //申请号
   @Id
    private String an;
    //公开号
    private String pn;
    //P_IPC
    @Column(name = "P_IPC")
    private String pIpc;
    //主分类号
    @Column(name = "P_IPC_MAIN")
    private String ipcMain;
    //C_IPC
    @Column(name = "C_IPC")
    private String cIpc;
    //对比文献主分类号
    @Column(name = "C_IPC_MAIN")
    private String cIpcMain;
    //案卷发明标题
    @Column(name = "P_INV_TITLE")
    private String pInvTitle;
    //引文发明标题
    @Column(name = "C_INV_TITLE")
    private String cInvTitle;
    //引文国别
    @Column(name = "CITED_PUB_CN")
    private String citedPubCn;
    //引文公开流水号
    @Column(name = "CITED_PUB_SN")
    private String citedPubSn;
    //引文公开类型
    @Column(name = "CITED_PUB_TYPE")
    private String citedPubType;
    //引文公开号
    @Column(name = "CITED_PN")
    private String citedPn;
    //引文申请号
    @Column(name = "NRD_AN")
    private String nrdAn;
    //引文类型
    @Column(name = "REFERENCE_CATEGORY")
    private String reference_category;

}
