package com.canteen.dao;


import com.canteen.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 用户数据访问接口
 * 负责用户数据的持久化操作
 */
@Mapper
public interface UserDAO {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，不存在返回 null
     */
    User findByUsername(String username);

    /**
     * 查找所有用户
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 保存用户（新增或更新）
     * @param user 用户对象
     * @return 是否保存成功
     */
    boolean save(User user);

    /**
     * 删除用户
     * @param username 用户名
     * @return 是否删除成功
     */
    boolean delete(String username);
}

