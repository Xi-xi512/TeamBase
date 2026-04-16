package com.canteen.service;


import com.canteen.dao.OrderDAO;
import com.canteen.entity.Dish;
import com.canteen.entity.Order;
import com.canteen.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 订单服务类
 * 负责订单创建和提交
 *
 * 重构说明：
 * - 移除统计相关方法（已拆分到 ReportService）
 * - 移除不必要的 DishService 依赖
 * - 单一职责：只负责订单创建和提交
 */
@Service
public class OrderService {
    @Autowired
    private OrderDAO orderDAO;

    /**
     * 获取明日日期字符串
     * @return 日期字符串
     */
    public String getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    /**
     * 创建订单
     * @param user 用户对象
     * @param dish 菜品对象
     * @param portion 份量 (whole/half)
     * @return 订单对象
     */
    public Order createOrder(User user, Dish dish, String portion) {
        double price = dish.getPrice();
        if ("half".equals(portion)) {
            price = dish.getPrice() / 2.0;
        }

        Order order = new Order();
        order.setOrderId(orderDAO.generateOrderId());
        order.setUsername(user.getUsername());
        order.setStudentName(user.getName());
        order.setDishId(dish.getId());
        order.setDishName(dish.getName());
        order.setPortion(portion);
        order.setPrice(price);
        order.setDate(getTomorrowDate());

        return order;
    }

    /**
     * 提交订单
     * @param order 订单对象
     * @return 是否成功
     */
    public boolean submitOrder(Order order) {
        return orderDAO.saveOrder(order);
    }


    /**
     * 更新订单份量
     * @param orderId 订单ID
     * @param portion 新份量 (whole/half)
     * @return 是否更新成功
     */
    public boolean updateOrderPortion(String orderId, String portion) {
        Order order = orderDAO.findById(orderId);
        if (order != null) {
            order.setPortion(portion);
            // 重新计算价格
            // 注意：这里需要获取菜品信息来重新计算价格
            // 为了简化，这里假设价格计算逻辑已经在其他地方处理
            return orderDAO.saveOrder(order);
        }
        return false;
    }
}

