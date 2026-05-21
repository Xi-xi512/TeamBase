package com.canteen.controller;

import com.canteen.dao.UserDAO;
import com.canteen.entity.Dish;
import com.canteen.entity.Order;
import com.canteen.entity.User;
import com.canteen.service.DishService;
import com.canteen.service.OrderService;
import com.canteen.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ReportService reportService;

    @MockBean
    private DishService dishService;

    @MockBean
    private UserDAO userDAO;

    @Test
    void listMyOrders_shouldRejectWhenUsernameMissing() throws Exception {
        mockMvc.perform(get("/api/orders/mine")
                        .param("username", " "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少用户名参数"));
    }

    @Test
    void listMyOrders_shouldRejectWhenUserNotFound() throws Exception {
        when(userDAO.findByUsername("ghost")).thenReturn(null);

        mockMvc.perform(get("/api/orders/mine")
                        .param("username", "ghost"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void listMyOrders_shouldRejectWhenNotStudent() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(get("/api/orders/mine")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅学生可查询个人订单"));
    }

    @Test
    void listMyOrders_shouldReturnOrdersForStudent() throws Exception {
        when(userDAO.findByUsername("stu1")).thenReturn(new User("stu1", "123", "student", "张三"));
        Order order = new Order();
        order.setOrderId("O1100");
        when(orderService.listOrdersByUsername("stu1")).thenReturn(java.util.List.of(order));

        mockMvc.perform(get("/api/orders/mine")
                        .param("username", "stu1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.orders[0].orderId").value("O1100"));
    }

    @Test
    void createOrder_shouldRejectWhenPortionInvalid() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        when(userDAO.findByUsername("stu1")).thenReturn(student);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","dishId":"D001","portion":"quarter"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("份量须为 whole 或 half"));

        verify(dishService, never()).getDishById(any());
        verify(orderService, never()).submitOrder(any());
    }

    @Test
    void createOrder_shouldRejectWhenDishNotInTomorrowMenu() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        Dish dish = new Dish("D001", "红烧肉", 20.0, "desc", "2099-01-01");
        when(userDAO.findByUsername("stu1")).thenReturn(student);
        when(dishService.getDishById("D001")).thenReturn(dish);
        when(orderService.getTomorrowDate()).thenReturn("2099-01-02");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","dishId":"D001","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("该菜品不在明日可订菜单中"));

        verify(orderService, never()).submitOrder(any());
    }

    @Test
    void createOrder_shouldReturnSuccessWhenSubmitSucceeded() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        Dish dish = new Dish("D001", "红烧肉", 20.0, "desc", "2099-01-02");
        Order order = new Order();
        order.setOrderId("O1001");
        order.setUsername("stu1");

        when(userDAO.findByUsername("stu1")).thenReturn(student);
        when(dishService.getDishById("D001")).thenReturn(dish);
        when(orderService.getTomorrowDate()).thenReturn("2099-01-02");
        when(orderService.createOrder(student, dish, "whole")).thenReturn(order);
        when(orderService.submitOrder(order)).thenReturn(true);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","dishId":"D001","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单创建成功"))
                .andExpect(jsonPath("$.order.orderId").value("O1001"));
    }

    @Test
    void cancelTomorrowOrder_shouldRejectWhenOrderBelongsToAnotherUser() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        Order order = new Order();
        order.setOrderId("O1002");
        order.setUsername("stu2");
        order.setDate("2099-01-02");

        when(userDAO.findByUsername("stu1")).thenReturn(student);
        when(orderService.getById("O1002")).thenReturn(order);

        mockMvc.perform(delete("/api/orders/O1002")
                        .param("username", "stu1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无权限取消该订单"));

        verify(orderService, never()).deleteOrder(any());
    }

    @Test
    void cancelTomorrowOrder_shouldRejectWhenUsernameMissing() throws Exception {
        mockMvc.perform(delete("/api/orders/O1002")
                        .param("username", " "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少用户名参数"));
    }

    @Test
    void cancelTomorrowOrder_shouldRejectWhenUserIsNotStudent() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(delete("/api/orders/O1002")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅学生可取消个人订单"));
    }

    @Test
    void cancelTomorrowOrder_shouldRejectWhenOrderNotFound() throws Exception {
        when(userDAO.findByUsername("stu1")).thenReturn(new User("stu1", "123", "student", "张三"));
        when(orderService.getById("O404")).thenReturn(null);

        mockMvc.perform(delete("/api/orders/O404")
                        .param("username", "stu1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单不存在"));
    }

    @Test
    void cancelTomorrowOrder_shouldRejectWhenNotTomorrowOrder() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        Order order = new Order();
        order.setOrderId("O1005");
        order.setUsername("stu1");
        order.setDate("2099-01-03");
        when(userDAO.findByUsername("stu1")).thenReturn(student);
        when(orderService.getById("O1005")).thenReturn(order);
        when(orderService.getTomorrowDate()).thenReturn("2099-01-02");

        mockMvc.perform(delete("/api/orders/O1005")
                        .param("username", "stu1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅可取消明日订单"));
    }

    @Test
    void cancelTomorrowOrder_shouldCancelWhenOwnerAndTomorrow() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        Order order = new Order();
        order.setOrderId("O1003");
        order.setUsername("stu1");
        order.setDate("2099-01-02");

        when(userDAO.findByUsername("stu1")).thenReturn(student);
        when(orderService.getById("O1003")).thenReturn(order);
        when(orderService.getTomorrowDate()).thenReturn("2099-01-02");
        when(orderService.deleteOrder("O1003")).thenReturn(true);

        mockMvc.perform(delete("/api/orders/O1003")
                        .param("username", "stu1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单已取消"));
    }

    @Test
    void createOrder_shouldRejectWhenUsernameMissing() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":" ","dishId":"D001","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少订餐账号"));
    }

    @Test
    void createOrder_shouldRejectWhenUserNotFound() throws Exception {
        when(userDAO.findByUsername("ghost")).thenReturn(null);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"ghost","dishId":"D001","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void createOrder_shouldRejectWhenUserNotStudent() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","dishId":"D001","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("仅学生账号可订餐"));
    }

    @Test
    void createOrder_shouldRejectWhenDishIdMissing() throws Exception {
        when(userDAO.findByUsername("stu1")).thenReturn(new User("stu1", "123", "student", "张三"));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","dishId":" ","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("请选择菜品"));
    }

    @Test
    void createOrder_shouldRejectWhenDishNotFound() throws Exception {
        when(userDAO.findByUsername("stu1")).thenReturn(new User("stu1", "123", "student", "张三"));
        when(dishService.getDishById("D404")).thenReturn(null);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","dishId":"D404","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("菜品不存在"));
    }

    @Test
    void createOrder_shouldReturnFailureWhenSubmitFailed() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        Dish dish = new Dish("D001", "红烧肉", 20.0, "desc", "2099-01-02");
        Order order = new Order();
        order.setOrderId("O1006");
        when(userDAO.findByUsername("stu1")).thenReturn(student);
        when(dishService.getDishById("D001")).thenReturn(dish);
        when(orderService.getTomorrowDate()).thenReturn("2099-01-02");
        when(orderService.createOrder(student, dish, "whole")).thenReturn(order);
        when(orderService.submitOrder(order)).thenReturn(false);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","dishId":"D001","portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单创建失败"));
    }

    @Test
    void updateOrderPortion_shouldReturnFailureWhenServiceReturnsFalse() throws Exception {
        when(orderService.updateOrderPortion("O1004", "half")).thenReturn(false);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/orders/O1004/portion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"portion":"half"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("订单份量更新失败"));
    }

    @Test
    void updateOrderPortion_shouldReturnSuccessWhenServiceReturnsTrue() throws Exception {
        when(orderService.updateOrderPortion("O1004", "whole")).thenReturn(true);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/orders/O1004/portion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"portion":"whole"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("订单份量更新成功"));
    }

    @Test
    void getStatistics_shouldRejectWhenRequesterNotAdmin() throws Exception {
        User student = new User("stu1", "123", "student", "张三");
        when(userDAO.findByUsername("stu1")).thenReturn(student);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/orders/statistics")
                        .param("username", "stu1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无权限查看统计报表"));
    }

    @Test
    void getStatistics_shouldRejectWhenUsernameMissing() throws Exception {
        mockMvc.perform(get("/api/orders/statistics")
                        .param("username", " "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少用户名参数"));
    }

    @Test
    void getStatistics_shouldReturnStatisticsWhenRequesterIsAdmin() throws Exception {
        User admin = new User("admin", "123", "admin", "管理员");
        when(userDAO.findByUsername("admin")).thenReturn(admin);
        when(reportService.getStatistics()).thenReturn(Map.of("totalOrders", 5));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/orders/statistics")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.statistics.totalOrders").value(5));
    }
}
