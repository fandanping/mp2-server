package com.neusoft.mpserver.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页对象
 * @name zhengchj
 * @email zhengchj@neusoft.com
 */
@Data
public class Pagination implements Serializable {
    private int start;
    private int size;
    private int total;
}
