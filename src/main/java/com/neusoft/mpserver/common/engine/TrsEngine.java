package com.neusoft.mpserver.common.engine;
import com.neusoft.mpserver.common.domain.Condition;
import com.neusoft.mpserver.common.domain.TrsResult;

/**
 * trs引擎接口
 * @name zhengchj
 * @email zhengchj@neusoft.com
 */
public interface TrsEngine {
    /**
     * 根据检索式检索
     * @param condition  检索条件
     * @return  TrsResult   检索结果
     */
    TrsResult search(Condition condition);


}
