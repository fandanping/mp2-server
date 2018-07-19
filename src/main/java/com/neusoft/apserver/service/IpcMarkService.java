package com.neusoft.apserver.service;

import com.neusoft.apserver.domain.IpcMark;
import java.util.List;

/**
 * 标引词模块：service层接口
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
public interface IpcMarkService {
    //保存标引词
    public boolean addMark(String userId, List<IpcMark> markList);

    //删除标引词
    public boolean deleteMark(String markId, String userId);

    //查询标引词
    public List<IpcMark> showMarkList(String an);
}
