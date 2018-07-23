package com.neusoft.mpserver.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * trs数据对象
 * @name zhengchj
 * @email zhengchj@neusoft.com
 */
@Data
public class Record implements Serializable {
    /**
     * 存储的trs数据
     */
    Map<String, String> dataMap = new HashMap<String, String>();
}
