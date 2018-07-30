package com.neusoft.mpserver.dao;

import com.neusoft.mpserver.domain.AddressRule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 规则： 数据操作层
 */
public interface AddressRuleRepository  extends JpaRepository<AddressRule,String>{


}
