package com.neusoft.mpserver.common.domain;

import lombok.Data;
import java.io.Serializable;

@Data
public class Condition implements Serializable {
    /**
     * 检索式
     */
    private String Exp;

    /**
     * 分页对象
     */
    private Pagination pagination = new Pagination();

    /**
     * 显示字段
     */
    private String displayFields;

    /**
     * 检索库
     */
    private String dbName;
    /**
     * 排序字段
     */
    private String sortFields;
}
