package com.canteen.service;


import com.canteen.dao.DishDAO;
import com.canteen.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 菜品服务类
 * 负责菜品数据管理
 *
 * 重构说明：
 * - 支持依赖注入，使用 DishDAO 访问数据
 * - 移除内存硬编码数据
 */
@Service
public class DishService {
    @Autowired
    private DishDAO dishDAO;

    /**
     * 获取所有菜品
     * @return 菜品列表
     */
    public List<Dish> getAllDishes() {
        return dishDAO.findAll();
    }

    /**
     * 根据 ID 获取菜品
     * @param dishId 菜品 ID
     * @return 菜品对象，不存在返回 null
     */
    public Dish getDishById(String dishId) {
        return dishDAO.findById(dishId);
    }

    /**
     * 打印菜品列表
     */
    public void printDishes() {
        List<Dish> dishes = getAllDishes();

        System.out.println("\n========== 明日菜品预览 ==========");
        System.out.printf("%-6s %-12s %-8s %s%n", "编号", "菜名", "价格 (整)", "描述");
        System.out.println("----------------------------------");
        for (int i = 0; i < dishes.size(); i++) {
            Dish dish = dishes.get(i);
            System.out.printf("%-6s %-12s %-8.2f %s%n",
                    (i + 1), dish.getName(), dish.getPrice(), dish.getDescription());
        }
        System.out.println("----------------------------------");
        System.out.println("💡 提示：半份价格为整份的一半");
        System.out.println("==================================\n");
    }
}

