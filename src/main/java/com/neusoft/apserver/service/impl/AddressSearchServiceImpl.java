package com.neusoft.apserver.service.impl;
import com.neusoft.apserver.dao.AddressRepository;
import com.neusoft.apserver.domain.AddressMark;
import com.neusoft.apserver.service.AddressSearchService;
import lombok.experimental.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 地址标引-查询模块：service层实现
 * 只有在这一层加事务管理才是真正的事务管理
 * @name fandp
 * @email fandp@neusoft.com
 */

@Service
public class AddressSearchServiceImpl  implements AddressSearchService{
    @Autowired
    private AddressRepository addressRepository;

    /**
     * 查询正在标引的地址
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public List<AddressMark> showMarkingList(String userId) {
        List<AddressMark>  addressMarkingList=addressRepository.findByMarkUser(userId);
        return addressMarkingList;
    }

    /**
     * 转换器
     * @param addressMark
     * @return
     */
     private Map<String ,List> reverseAddressMark(List<Object[]> addressMark){
            Map<String ,List> map=new HashMap<String,List>();
             List<AddressMark> addressMarkList=new ArrayList<AddressMark>();
             List<String> idList=new ArrayList();
             for(int i=0;i<addressMark.size();i++){
                 AddressMark address=new AddressMark();
                 address.setId(addressMark.get(i)[0].toString());
                 address.setAn(addressMark.get(i)[1].toString());
                 address.setAddress(addressMark.get(i)[2].toString());
                 address.setZip(addressMark.get(i)[3].toString());
                 addressMarkList.add(address);
                 idList.add(addressMark.get(i)[0].toString());
             }
             map.put("addressMarkList",addressMarkList);
             map.put("idList",idList);
             System.out.print("aaaaaaaaaaaaaa"+map);
             return map;
         }
    /**
     * 随机top20篇，可以模糊查询前20篇
     * @param keyword
     * @param userId
     * @return
     */
    @Transactional
    @Override
    public List<AddressMark> showUnMarkList(String userId,String keyword) {
        addressRepository.updateMarkStatus(userId);
        System.out.print("aaaaaaaaaaaaaa");
        if(keyword ==null){
            List<Object[]> addressMark =addressRepository.findByRandom();
            List<AddressMark>  result=this.reverseAddressMark(addressMark).get("addressMarkList");
            System.out.println(result);
            List<String> idList=this.reverseAddressMark(addressMark).get("idList");
            addressRepository.updateMarkUser(idList,userId);
            return  result;
        }else{
            List<Object[]> addressMark =addressRepository.findByAddress(keyword);
            return null;
            /*List<AddressMark>  result=this.reverseAddressMark(addressMark).get("addressMarkList");
            List<String> idList=this.reverseAddressMark(addressMark).get("idList");
            addressRepository.updateMarkUser(idList,userId);
            return result;*/
        }
    }

    /**
     * 保存标引词updateMarkUser
     * @param userId
     * @param markList
     * @return
     */
    @Transactional
    @Override
    public boolean addMark(String userId, List<AddressMark> markList) {
        List<AddressMark> markListResult=markList;
        for(int i=0;i<markListResult.size();i++){
            if(markListResult.get(i).getMarked() =="1"){
                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                markListResult.get(i).setMarkTime(df.format(day));
            }else{
                String id=markListResult.get(i).getId();
                addressRepository.updateMarkStatusById(id);
            }
        }
        if(addressRepository.saveMark(markListResult).isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}
