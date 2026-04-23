package com.canteen.controller;

import com.canteen.dao.UserDAO;
import com.canteen.entity.Order;
import com.canteen.entity.User;
import com.canteen.entity.Dish;
import com.canteen.service.OrderService;
import com.canteen.service.ReportService;
import com.canteen.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DishService dishService;

    @Autowired
    private UserDAO userDAO;

    @GetMapping("/mine")
    public Map<String, Object> listMyOrders(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "缺少用户名参数");
            return response;
        }
        User u = userDAO.findByUsername(username.trim());
        if (u == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }
        if (!"student".equals(u.getRole())) {
            response.put("success", false);
            response.put("message", "仅学生可查询个人订单");
            return response;
        }
        List<Order> orders = orderService.listOrdersByUsername(u.getUsername());
        response.put("success", true);
        response.put("orders", orders);
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> cancelTomorrowOrder(
            @PathVariable String id,
            @RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "缺少用户名参数");
            return response;
        }
        User u = userDAO.findByUsername(username.trim());
        if (u == null || !"student".equals(u.getRole())) {
            response.put("success", false);
            response.put("message", "仅学生可取消个人订单");
            return response;
        }
        Order order = orderService.getById(id);
        if (order == null) {
            response.put("success", false);
            response.put("message", "订单不存在");
            return response;
        }
        if (!u.getUsername().equals(order.getUsername())) {
            response.put("success", false);
            response.put("message", "无权限取消该订单");
            return response;
        }
        if (!orderService.getTomorrowDate().equals(order.getDate())) {
            response.put("success", false);
            response.put("message", "仅可取消明日订单");
            return response;
        }
        boolean ok = orderService.deleteOrder(id);
        response.put("success", ok);
        response.put("message", ok ? "订单已取消" : "取消失败，请稍后重试");
        return response;
    }

    @PostMapping
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> orderData) {
        String username = stringParam(orderData.get("username"));
        String dishId = stringParam(orderData.get("dishId"));
        String portion = stringParam(orderData.get("portion"));

        Map<String, Object> response = new HashMap<>();

        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "缺少订餐账号");
            return response;
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return response;
        }
        if (!"student".equals(user.getRole())) {
            response.put("success", false);
            response.put("message", "仅学生账号可订餐");
            return response;
        }

        if (portion == null || (!"whole".equals(portion) && !"half".equals(portion))) {
            response.put("success", false);
            response.put("message", "份量须为 whole 或 half");
            return response;
        }

        if (dishId == null || dishId.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择菜品");
            return response;
        }

        Dish dish = dishService.getDishById(dishId.trim());
        if (dish == null) {
            response.put("success", false);
            response.put("message", "菜品不存在");
            return response;
        }

        String tomorrow = orderService.getTomorrowDate();
        if (dish.getMenuDate() == null || !tomorrow.equals(dish.getMenuDate())) {
            response.put("success", false);
            response.put("message", "该菜品不在明日可订菜单中");
            return response;
        }

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
    public Map<String, Object> getStatistics(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "缺少用户名参数");
            return response;
        }
        User requester = userDAO.findByUsername(username.trim());
        if (requester == null || !"admin".equals(requester.getRole())) {
            response.put("success", false);
            response.put("message", "无权限查看统计报表");
            return response;
        }
        Map<String, Object> stats = reportService.getStatistics();
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

    private static String stringParam(Object raw) {
        if (raw == null) {
            return null;
        }
        String s = String.valueOf(raw).trim();
        return s.isEmpty() ? null : s;
    }
}
