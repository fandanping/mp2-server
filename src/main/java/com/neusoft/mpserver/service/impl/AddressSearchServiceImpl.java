package com.neusoft.mpserver.service.impl;
import com.neusoft.mpserver.common.util.IDGenerator;
import com.neusoft.mpserver.dao.AddressFormRepository;
import com.neusoft.mpserver.dao.AddressRepository;
import com.neusoft.mpserver.dao.AddressRuleRepository;
import com.neusoft.mpserver.domain.AddressMark;
import com.neusoft.mpserver.domain.AddressMarkForm;
import com.neusoft.mpserver.domain.AddressRule;
import com.neusoft.mpserver.service.AddressSearchService;
import org.springframework.beans.factory.annotation.Autowired;
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
    /**
     * 查询正在标引的地址,若没有正在标引的词，随机查询20篇
     *
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public Map<String,Object> showMarkingList(String userId) {
        Map<String ,Object> result = new HashMap<String ,Object>();
        List<Object[]> addressMarkingList = addressRepository.findByMarkUser(userId);
        if (addressMarkingList.isEmpty()) {
            List<Object[]> addressMark = addressRepository.findByRandom();
            List<String> idList = this.reverseAddressMark(addressMark).get("idList");
            List<AddressMark> addressMarkList = this.reverseAddressMark(addressMark).get("addressMarkList");
            addressRepository.updateMarkUser(idList, userId);
            result.put("addressMarkList",addressMarkList);
            return result;
        } else {
            List<AddressMark> addressMarkList = this.reverseAddressMark(addressMarkingList).get("addressMarkList");
            result.put("addressMarkList",addressMarkList);
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
            Object[] param=addressMark.get(i);
            AddressMark address = new AddressMark();
            address.setId(param[0].toString());
            address.setAn(param[1].toString());
            if (param[2] == null) {
                address.setAddress("");
            } else {
                address.setAddress(param[2].toString());
            }
            if(param[3] ==null){
                address.setZip("");
            }else{
                address.setZip(param[3].toString());
            }
            if(param[4] ==null){
                address.setAppName("");
            }else{
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
    public Map<String,Object> showUnMarkList(String userId, String keyword) {
        addressRepository.updateMarkStatus(userId);
        Map<String ,Object> result = new HashMap<String ,Object>();
        if (keyword == null) {
            List<Object[]> addressMark = addressRepository.findByRandom();
            List<String> idList = this.reverseAddressMark(addressMark).get("idList");
            List<AddressMark> addressMarkList = this.reverseAddressMark(addressMark).get("addressMarkList");
            addressRepository.updateMarkUser(idList, userId);
            result.put("addressMarkList",addressMarkList);
            return result;
        } else {
            List<Object[]> addressMark = addressRepository.findByMarkedAndAddressLike("%" + keyword + "%");
            if (addressMark.size() == 0) {
                result.put("addressMarkList","");
                return result;
            } else {
                List<String> idList = this.reverseAddressMark(addressMark).get("idList");
                List<AddressMark> addressMarkList = this.reverseAddressMark(addressMark).get("addressMarkList");
                addressRepository.updateMarkUser(idList, userId);
                result.put("addressMarkList",addressMarkList);
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
    public boolean addMark(String userId, List<AddressMarkForm> markList,List<AddressRule> ruleList) {
        List<AddressMarkForm> markListResult = new ArrayList<AddressMarkForm>();
        for (int i = 0; i < markList.size(); i++) {
            AddressMarkForm addressMark = markList.get(i);
            if (addressMark.getMarked().equals("1")) {
                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date=df.format(day);
                //精准匹配的数据库里全部置，去除重复数据
                int count=addressFormRepository.updateSameAddress(userId,addressMark.getProvince(),addressMark.getCity(),addressMark.getArea(),addressMark.getTown(),date,addressMark.getStatus(),addressMark.getAppName(),addressMark.getAddress());
            }
        }
        if(ruleList.size()!=0){
            for(int j=0;j<ruleList.size();j++){
                AddressRule rule=ruleList.get(j);
                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String d=df.format(day);
                try {
                    rule.setCreateTime(df.parse(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rule.setId(IDGenerator.generate());
            }
            List<AddressRule> saveRuleResult=addressRuleRepository.saveAll(ruleList);
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
}
