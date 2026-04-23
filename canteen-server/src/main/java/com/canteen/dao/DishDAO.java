package com.canteen.dao;


import com.canteen.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 菜品数据访问接口
 * 负责菜品数据的持久化操作
 */
@Mapper
public interface DishDAO {
    /**
     * 根据 ID 查找菜品
     * @param dishId 菜品 ID
     * @return 菜品对象，不存在返回 null
     */
    Dish findById(String dishId);

    /**
     * 查找所有菜品
     * @return 菜品列表
     */
    List<Dish> findAll();

    /**
     * 按可订日期查询菜品（明日菜单）
     */
    List<Dish> findByMenuDate(String menuDate);

    /**
     * 保存菜品（新增或更新）
     * @param dish 菜品对象
     * @return 是否保存成功
     */
    boolean save(Dish dish);

    /**
     * 删除菜品
     * @param dishId 菜品 ID
     * @return 是否删除成功
     */
    boolean delete(String dishId);
}
