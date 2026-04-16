package com.canteen.controller;

import com.canteen.entity.Order;
import com.canteen.entity.User;
import com.canteen.entity.Dish;
import com.canteen.service.OrderService;
import com.canteen.service.ReportService;
import com.canteen.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DishService dishService;

    @PostMapping
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> orderData) {
        String username = (String) orderData.get("username");
        String dishId = (String) orderData.get("dishId");
        String portion = (String) orderData.get("portion");

        // 获取菜品信息
        Dish dish = dishService.getDishById(dishId);
        Map<String, Object> response = new HashMap<>();

        if (dish == null) {
            response.put("success", false);
            response.put("message", "菜品不存在");
            return response;
        }

        // 创建用户对象（实际应该从登录状态获取）
        User user = new User();
        user.setUsername(username);
        user.setName("测试用户"); // 实际应该从数据库获取

        // 创建并提交订单
        Order order = orderService.createOrder(user, dish, portion);
        boolean success = orderService.submitOrder(order);

        if (success) {
            response.put("success", true);
            response.put("message", "订单创建成功");
            response.put("order", order);
        } else {
            response.put("success", false);
            response.put("message", "订单创建失败");
        }

        return response;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = reportService.getStatistics();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("statistics", stats);
        return response;
    }

    @PutMapping("/{id}/portion")
    public Map<String, Object> updateOrderPortion(@PathVariable String id, @RequestBody Map<String, String> portionUpdate) {
        String portion = portionUpdate.get("portion");
        boolean result = orderService.updateOrderPortion(id, portion);
        Map<String, Object> response = new HashMap<>();

        if (result) {
            response.put("success", true);
            response.put("message", "订单份量更新成功");
        } else {
            response.put("success", false);
            response.put("message", "订单份量更新失败");
        }

        return response;
    }
}
