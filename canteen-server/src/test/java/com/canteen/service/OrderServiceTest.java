package com.canteen.service;

import com.canteen.dao.OrderDAO;
import com.canteen.entity.Dish;
import com.canteen.entity.Order;
import com.canteen.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderHalfPortion_shouldUseHalfPriceAndPopulateFields() {
        User user = new User("student1", "123456", "student", "学生1");
        Dish dish = new Dish("D001", "宫保鸡丁", 16.0, "desc", "2099-01-01");
        when(orderDAO.generateOrderId()).thenReturn("ORD0001");

        Order order = orderService.createOrder(user, dish, "half");

        assertEquals("ORD0001", order.getOrderId());
        assertEquals("student1", order.getUsername());
        assertEquals("学生1", order.getStudentName());
        assertEquals("D001", order.getDishId());
        assertEquals("宫保鸡丁", order.getDishName());
        assertEquals("half", order.getPortion());
        assertEquals(8.0, order.getPrice(), 0.0001);
        assertEquals(orderService.getTomorrowDate(), order.getDate());
        assertNotNull(order.getCreatedAt());
        verify(orderDAO).generateOrderId();
    }

    @Test
    void updateOrderPortion_whenOrderExists_shouldSaveUpdatedOrder() {
        Order existing = new Order();
        existing.setOrderId("ORD0002");
        existing.setPortion("whole");
        when(orderDAO.findById("ORD0002")).thenReturn(existing);
        when(orderDAO.saveOrder(existing)).thenReturn(true);

        boolean result = orderService.updateOrderPortion("ORD0002", "half");

        assertTrue(result);
        assertEquals("half", existing.getPortion());
        verify(orderDAO).findById("ORD0002");
        verify(orderDAO).saveOrder(existing);
    }
}
