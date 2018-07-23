package com.neusoft.mpserver.dao;

import com.neusoft.mpserver.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * user操作数据库层
 * @name fandp
 * @email fandp@neusoft.com
 */
public interface UserRepository   extends JpaRepository<User,String> {
    public List<User> findByUsername(String username);
    public User findByUsernameAndPassword(String username, String password);
}
