package com.neusoft.mpserver.sipo57.service;
import com.neusoft.mpserver.sipo57.domain.AddressMarkForm;
import com.neusoft.mpserver.sipo57.domain.AddressRule;
import com.neusoft.mpserver.sipo57.domain.AddressRuleForm;
import java.util.List;
import java.util.Map;

/**
 * 地址标引模块：service层接口
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
public interface AddressSearchService {
    //查询正在标引列表
    public Map<String,Object> showMarkingList(String userId);
    //查询未标引地址列表
    public Map<String,Object> showUnMarkList(String userId, String keyword);
    //保存标引词
    public boolean addMark(String userId, List<AddressMarkForm> markList, List<AddressRuleForm> ruleList);
    //查询今天所有规则,不带分页
    public Map<String,Object> showRuleList();
    //查询今天所有规则，带分页
    public Map<String,Object> showRulePageList(String userId, String type, String keyword, int pageNumber, int size);
    //修改标引规则（只能修改自己的）
    public boolean ModifyRule(String userId, AddressRule rule);

    boolean deleteRule(String id, String userId);
}
