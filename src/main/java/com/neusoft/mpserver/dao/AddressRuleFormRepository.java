package com.neusoft.mpserver.dao;
import com.neusoft.mpserver.domain.AddressRuleForm;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 规则： 数据操作层
 */
public interface AddressRuleFormRepository extends JpaRepository<AddressRuleForm, String> {

}
