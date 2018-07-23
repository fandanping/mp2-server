package com.neusoft.mpserver.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * trs结果集
 * @name zhengchj
 * @email zhengchj@neusoft.com
 */
@Data
public class TrsResult implements Serializable {
    /**
     * 分页对象
     */
    private Pagination pagination;

    private boolean last = false;

    /**
     * 数据集合
     */
    List<Record> records = new ArrayList<Record>();


}
