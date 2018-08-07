package com.neusoft.mpserver.service.impl;

import com.neusoft.mpserver.common.domain.Pagination;
import com.neusoft.mpserver.common.util.IDGenerator;
import com.neusoft.mpserver.dao.AddressFormRepository;
import com.neusoft.mpserver.dao.AddressRepository;
import com.neusoft.mpserver.dao.AddressRuleFormRepository;
import com.neusoft.mpserver.dao.AddressRuleRepository;
import com.neusoft.mpserver.domain.AddressMark;
import com.neusoft.mpserver.domain.AddressMarkForm;
import com.neusoft.mpserver.domain.AddressRule;
import com.neusoft.mpserver.domain.AddressRuleForm;
import com.neusoft.mpserver.service.AddressSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 地址标引-查询模块：service层实现
 * 只有在这一层加事务管理才是真正的事务管理
 *
 * @name fandp
 * @email fandp@neusoft.com
 */
@Service
public class AddressSearchServiceImpl implements AddressSearchService {

    @Autowired
    private AddressRepository addressRepository;
    //@PersistenceContext
    //private EntityManager em;
    @Autowired
    private AddressFormRepository addressFormRepository;
    @Autowired
    private AddressRuleRepository addressRuleRepository;
    @Autowired
    private AddressRuleFormRepository addressRuleFormRepository;


    /**
     * 查询正在标引的地址,若没有正在标引的词，随机查询20篇
     *
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public Map<String, Object> showMarkingList(String userId) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Object[]> addressMarkingList = addressRepository.findByMarkUser(userId);
        if (addressMarkingList.isEmpty()) {
            List<Object[]> addressMark = addressRepository.findByRandom();
            List<String> idList = this.reverseAddressMark(addressMark).get("idList");
            List<AddressMark> addressMarkList = this.reverseAddressMark(addressMark).get("addressMarkList");
            addressRepository.updateMarkUser(idList, userId);
            result.put("addressMarkList", addressMarkList);
            return result;
        } else {
            List<AddressMark> addressMarkList = this.reverseAddressMark(addressMarkingList).get("addressMarkList");
            result.put("addressMarkList", addressMarkList);
            return result;
        }

    }

    /**
     * 转换器
     *
     * @param addressMark
     * @return
     */
    private Map<String, List> reverseAddressMark(List<Object[]> addressMark) {
        Map<String, List> map = new HashMap<String, List>();
        List<AddressMark> addressMarkList = new ArrayList<AddressMark>();
        List<String> idList = new ArrayList();
        for (int i = 0; i < addressMark.size(); i++) {
            Object[] param = addressMark.get(i);
            AddressMark address = new AddressMark();
            address.setId(param[0].toString());
            address.setAn(param[1].toString());
            if (param[2] == null) {
                address.setAddress("");
            } else {
                address.setAddress(param[2].toString());
            }
            if (param[3] == null) {
                address.setZip("");
            } else {
                address.setZip(param[3].toString());
            }
            if (param[4] == null) {
                address.setAppName("");
            } else {
                address.setAppName(param[4].toString());
            }
            addressMarkList.add(address);
            idList.add(param[0].toString());
        }
        map.put("addressMarkList", addressMarkList);
        map.put("idList", idList);
        return map;
    }

    /**
     * 随机top20篇，也可以模糊查询前20篇
     *
     * @param keyword
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public Map<String, Object> showUnMarkList(String userId, String keyword) {
        addressRepository.updateMarkStatus(userId);
        Map<String, Object> result = new HashMap<String, Object>();
        if (keyword == null) {
            List<Object[]> addressMark = addressRepository.findByRandom();
            List<String> idList = this.reverseAddressMark(addressMark).get("idList");
            List<AddressMark> addressMarkList = this.reverseAddressMark(addressMark).get("addressMarkList");
            addressRepository.updateMarkUser(idList, userId);
            result.put("addressMarkList", addressMarkList);
            return result;
        } else {
            List<Object[]> addressMark = addressRepository.findByMarkedAndAddressLike("%" + keyword + "%");
            if (addressMark.size() == 0) {
                result.put("addressMarkList", "");
                return result;
            } else {
                List<String> idList = this.reverseAddressMark(addressMark).get("idList");
                List<AddressMark> addressMarkList = this.reverseAddressMark(addressMark).get("addressMarkList");
                addressRepository.updateMarkUser(idList, userId);
                result.put("addressMarkList", addressMarkList);
                return result;
            }
        }
    }

    /**
     * 保存标引词updateMarkUser
     *
     * @param userId
     * @param markList
     * @return
     */
    @Transactional
    @Override
    public boolean addMark(String userId, List<AddressMarkForm> markList, List<AddressRuleForm> ruleList) {
        List<AddressMarkForm> markListResult = new ArrayList<AddressMarkForm>();
        for (int i = 0; i < markList.size(); i++) {
            AddressMarkForm addressMark = markList.get(i);
            if (addressMark.getMarked().equals("1")) {
                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = df.format(day);
                //精准匹配的数据库里全部置，去除重复数据
                int count = addressFormRepository.updateSameAddress(userId, addressMark.getProvince(), addressMark.getCity(), addressMark.getArea(), addressMark.getTown(), date, addressMark.getStatus(), addressMark.getAppName(), addressMark.getAddress());
            }
            //暂不标引
            if (addressMark.getMarked().equals("4")) {
                int noMark = addressFormRepository.updateNoMarkAddress(addressMark.getAppName(), addressMark.getAddress());
            }
            //已有标引规则，等待晚上更新
            if (addressMark.getMarked().equals("5")) {
                int waitMark = addressFormRepository.waitMarkAddress(addressMark.getId());
            }

        }
        if (ruleList.size() != 0) {
            for (int j = 0; j < ruleList.size(); j++) {
                AddressRuleForm rule = ruleList.get(j);
                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String d = df.format(day);
                try {
                    rule.setCreateTime(df.parse(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rule.setId(IDGenerator.generate());
                rule.setUserId(userId);
            }
            List<AddressRuleForm> saveRuleResult = addressRuleFormRepository.saveAll(ruleList);
        }
        //另一种写法
       /* for(AddressMarkForm mark : markListResult){
            em.merge(mark);
        }
        em.flush();
        em.clear();*/
     /*  if(idList.size() !=0){
           int updateStatus = addressFormRepository.updateMarkStatusById(idList);
       }*/

        return true;
    }

    /**
     * 查询今天的所有规则，不带分页
     *
     * @return
     */
    @Transactional
    @Override
    public Map<String, Object> showRuleList() {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String time = df.format(day) + " 06:00:00";
        System.out.println("!!!!" + time);
        List<Object[]> ruleList = addressRuleRepository.queryRule(time);
        return this.reverseRule(ruleList);
    }

    /**
     * 查询今天的所有规则，带分页
     */
    @Transactional
    @Override
    public Map<String, Object> showRulePageList(String userId, String type, String keyword, int pageNumber, int size) {
        Map<String, Object> map = new HashMap<String, Object>();
        //拼接日期
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String time = df.format(day) + " 06:00:00";
        //查总数
        Pagination pageination = new Pagination();
        pageination.setPageNumber(pageNumber);
        pageination.setSize(size);
        pageination.setStart(pageNumber * size + 1);
        int total = 0;
        //分页查询数据
        Pageable pageable = new PageRequest(pageNumber, size);
        List<Object[]> pageResult = new ArrayList<Object[]>();
        if (type.equals("1")) {  //查全部
            if (keyword != null && !keyword.equals("")) {
                total = addressRuleRepository.findRuleAllCountBykey(time,"%" + keyword + "%");
                pageResult = addressRuleRepository.findRuleAllBykey(time, "%" + keyword + "%", pageable);
            } else {
                total = addressRuleRepository.findRuleAllCount(time);
                pageResult = addressRuleRepository.findRuleAll(time, pageable);
            }
        } else if (type.equals("2")) { //查自己
            if (keyword != null && !keyword.equals("")) {
                total = addressRuleRepository.findRuleMeCountBykey(time, userId,"%" + keyword + "%");
                pageResult = addressRuleRepository.findRuleMeBykey(time, "%" + keyword + "%", userId, pageable);
            } else {
                total = addressRuleRepository.findRuleMeCount(time, userId);
                pageResult = addressRuleRepository.findRuleMe(time, userId, pageable);
            }
        } else if (type.equals("3")) {  //查其他人
            if (keyword != null && !keyword.equals("")) {
                total = addressRuleRepository.findRuleOtherCount(time, userId);
                pageResult = addressRuleRepository.findRuleOtherBykey(time, "%" + keyword + "%", userId, pageable);
            } else {
                total = addressRuleRepository.findRuleOtherCount(time, userId);
                pageResult = addressRuleRepository.findRuleOther(time, userId, pageable);
            }
        }
        pageination.setTotal(total);
        map.put("pagination", pageination);
        map.put("addressRuleList", this.reverseRuleALL(pageResult));
        return map;
    }

    /**
     * 修改规则
     *
     * @param userId
     * @param rule
     * @return
     */
    @Transactional
    @Override
    public boolean ModifyRule(String userId, AddressRule rule) {
        addressRuleRepository.updateRule(userId, rule.getId(), rule.getProvince(), rule.getCity(), rule.getArea(), rule.getRule());
        return true;
    }

    /**
     * 规则转换器:传给前台带地址
     *
     * @param ruleList
     * @return
     */
    private List<AddressRule> reverseRuleALL(List<Object[]> ruleList) {
        List<AddressRule> addressRuleList = new ArrayList<AddressRule>();
        for (int i = 0; i < ruleList.size(); i++) {
            Object[] param = ruleList.get(i);
            AddressRule rule = new AddressRule();
            rule.setId(param[0].toString());
            if (param[1] == null) {
                rule.setAddress("");
            } else {
                rule.setAddress(param[1].toString());
            }
            if (param[2] == null) {
                rule.setRule("");
            } else {
                rule.setRule(param[2].toString());
            }
            if (param[3] == null) {
                rule.setProvince("");
            } else {
                rule.setProvince(param[3].toString());
            }
            if (param[4] == null) {
                rule.setCity("");
            } else {
                rule.setCity(param[4].toString());
            }
            if (param[5] == null) {
                rule.setArea("");
            } else {
                rule.setArea(param[5].toString());
            }
            if (param[6] == null) {
                rule.setUserId("");
            } else {
                rule.setUserId(param[6].toString());
            }
            if (param[7] == null) {
                rule.getUser().setUsername("");
            } else {
                rule.getUser().setUsername(param[7].toString());
            }
            addressRuleList.add(rule);
        }
        return addressRuleList;
    }

    /**
     * 规则转换器
     *
     * @param ruleList
     * @return
     */
    private Map<String, Object> reverseRule(List<Object[]> ruleList) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<AddressRule> addressRuleList = new ArrayList<AddressRule>();
        for (int i = 0; i < ruleList.size(); i++) {
            Object[] param = ruleList.get(i);
            AddressRule rule = new AddressRule();
            rule.setId(param[0].toString());
            rule.setRule(param[1].toString());
            rule.setProvince(param[2].toString());
            rule.setCity(param[3].toString());
            if (param[4] == null) {
                rule.setArea("");
            } else {
                rule.setArea(param[4].toString());
            }
            if (param[5] == null) {
                rule.setUserId("");
            } else {
                rule.setUserId(param[5].toString());
            }
            if (param[6] == null) {
                rule.getUser().setUsername("");
            } else {
                rule.getUser().setUsername(param[6].toString());
            }
            addressRuleList.add(rule);
        }
        map.put("addressRuleList", addressRuleList);
        return map;
    }
}
