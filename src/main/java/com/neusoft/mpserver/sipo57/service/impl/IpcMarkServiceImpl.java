package com.neusoft.mpserver.sipo57.service.impl;


import com.neusoft.mpserver.common.util.IDGenerator;
import com.neusoft.mpserver.sipo57.dao.IpcMarkRepository;
import com.neusoft.mpserver.sipo57.domain.IpcMark;
import com.neusoft.mpserver.sipo57.service.IpcMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 标引词模块：service层实现
 * 只有在这一层加事务管理才是真正的事务管理
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@Service
public class IpcMarkServiceImpl implements IpcMarkService {
    @Autowired
    private IpcMarkRepository markRerpository;

    //保存标引词方法
    @Override
    @Transactional
    public boolean addMark(String userId, List<IpcMark> markList) {
        List<IpcMark> markListResult = markList;
        for (int i = 0; i < markListResult.size(); i++) {
            markListResult.get(i).setId(IDGenerator.generate());
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            markListResult.get(i).setCreateTime(df.format(day));
        }
        if (markRerpository.saveAll(markListResult).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    //删除标引词方法
    @Transactional
    @Override
    public boolean deleteMark(String markId, String userId) {
        markRerpository.deleteMarkByIdAndUserId(markId, userId);
        return true;
    }

    //查询标引词
    @Transactional
    @Override
    public List<IpcMark> showMarkList(String an) {
        List<IpcMark> markList = markRerpository.findByAn(an);
        return markList;
    }


}
