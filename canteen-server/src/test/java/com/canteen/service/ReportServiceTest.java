package com.canteen.service;

import com.canteen.dao.OrderDAO;
import com.canteen.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getStatisticsForServingDate_shouldAggregateStudentsAndDishPortions() {
        String servingDate = "2099-01-02";
        Order o1 = buildOrder("student1", "宫保鸡丁", "whole", servingDate);
        Order o2 = buildOrder("student2", "宫保鸡丁", "half", servingDate);
        Order o3 = buildOrder("student1", "红烧肉", "half", servingDate);
        Order o4 = buildOrder("student3", "宫保鸡丁", "whole", "2099-01-03");
        when(orderDAO.readAllOrders()).thenReturn(List.of(o1, o2, o3, o4));

        Map<String, Object> stats = reportService.getStatisticsForServingDate(servingDate);

        assertEquals(2, stats.get("totalStudents"));
        assertEquals(3, stats.get("totalOrders"));
        @SuppressWarnings("unchecked")
        Map<String, Double> dishStats = (Map<String, Double>) stats.get("dishStats");
        assertEquals(1.5, dishStats.get("宫保鸡丁"), 0.0001);
        assertEquals(0.5, dishStats.get("红烧肉"), 0.0001);
    }

    private Order buildOrder(String username, String dishName, String portion, String date) {
        Order order = new Order();
        order.setUsername(username);
        order.setDishName(dishName);
        order.setPortion(portion);
        order.setDate(date);
        return order;
    }
}
