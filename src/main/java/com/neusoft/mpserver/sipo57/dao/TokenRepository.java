package com.neusoft.mpserver.sipo57.dao;

import com.neusoft.mpserver.sipo57.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * token 操作数据库层
 *
 * @name fandp
 * @email fandp@neusoft.com
 */

public interface TokenRepository extends JpaRepository<Token, String> {

    public Token findByTokenId(String tokenId);

    //退出功能
    @Modifying
    @Query(value = "delete from Token where userId=?1")
    public void deleteByUserId(String userId);


}
