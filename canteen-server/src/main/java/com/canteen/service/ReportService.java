package com.canteen.service;


import com.canteen.dao.OrderDAO;
import com.canteen.entity.Order;
import com.canteen.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计报表服务类
 * 负责订餐数据的统计和报表生成
 *
 * 重构说明：
 * - 从 OrderService 拆分出来的独立服务
 * - 单一职责：只负责统计和报表
 */
@Service
public class ReportService {
    @Autowired
    private OrderDAO orderDAO;

    /**
     * 获取统计报表数据
     * @return 统计结果 Map
     */
    public Map<String, Object> getStatistics() {
        return getStatisticsForServingDate(DateUtils.tomorrowYyyyMmDd());
    }

    /**
     * 指定供餐日的订餐统计（用于管理员按明日备餐）
     */
    public Map<String, Object> getStatisticsForServingDate(String servingDate) {
        List<Order> orders = orderDAO.readAllOrders().stream()
                .filter(o -> servingDate != null && servingDate.equals(o.getDate()))
                .collect(Collectors.toList());

        Map<String, Double> dishStats = new HashMap<>();  // 菜品名称 -> 总份数
        Set<String> uniqueStudents = new HashSet<>();  // 去重学生

        for (Order order : orders) {
            // 统计学生人数（去重）
            String username = order.getUsername();
            if (username != null) {
                uniqueStudents.add(username);
            }

            // 统计菜品份量
            String dishName = order.getDishName();
            if (dishName != null) {
                double portion = "half".equals(order.getPortion()) ? 0.5 : 1.0;
                dishStats.merge(dishName, portion, Double::sum);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalStudents", uniqueStudents.size());
        result.put("dishStats", dishStats);
        result.put("totalOrders", orders.size());
        result.put("servingDate", servingDate);

        return result;
    }

    /**
     * 打印统计报表
     */
    public void printReport() {
        Map<String, Object> stats = getStatistics();

        System.out.println("\n========== 订餐统计报表 ==========");
        System.out.println("📅 供餐日：" + stats.get("servingDate"));
        System.out.println("📊 订餐总人数：" + stats.get("totalStudents") + " 人");
        System.out.println("📝 订单总数：" + stats.get("totalOrders") + " 单");
        System.out.println("----------------------------------");
        System.out.println("🍽️  各菜品需求量：");

        @SuppressWarnings("unchecked")
        Map<String, Double> dishStats = (Map<String, Double>) stats.get("dishStats");

        if (dishStats.isEmpty()) {
            System.out.println("  暂无订单数据");
        } else {
            for (Map.Entry<String, Double> entry : dishStats.entrySet()) {
                String dishName = entry.getKey();
                double total = entry.getValue();
                String portionText = total % 1 == 0 ?
                        String.format("%.0f 整份", total) :
                        String.format("%.1f 份", total);
                System.out.printf("  - %-12s: %s%n", dishName, portionText);
            }
        }

        System.out.println("==================================\n");
    }
}

