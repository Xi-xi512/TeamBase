package com.canteen.controller;

import com.canteen.dao.OrderDAO;
import com.canteen.dao.UserDAO;
import com.canteen.entity.Dish;
import com.canteen.entity.User;
import com.canteen.service.DishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminDishController.class)
class AdminDishControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private DishService dishService;

    @MockBean
    private OrderDAO orderDAO;

    @Test
    void list_shouldRejectWhenNotAdmin() throws Exception {
        when(userDAO.findByUsername("stu1")).thenReturn(new User("stu1", "123", "student", "张三"));

        mockMvc.perform(get("/api/admin/dishes").param("username", "stu1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("无权限管理菜品"));

        verify(dishService, never()).getAllDishes();
    }

    @Test
    void list_shouldQueryByMenuDateWhenProvided() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));
        when(dishService.listByMenuDate("2099-01-02")).thenReturn(
                List.of(new Dish("D001", "鱼香肉丝", 18.0, "desc", "2099-01-02"))
        );

        mockMvc.perform(get("/api/admin/dishes")
                        .param("username", "admin")
                        .param("menuDate", "2099-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.dishes[0].id").value("D001"));
    }

    @Test
    void list_shouldReturnAllWhenMenuDateNotProvided() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));
        when(dishService.getAllDishes()).thenReturn(
                List.of(new Dish("D010", "青椒肉丝", 15.0, "desc", "2099-01-03"))
        );

        mockMvc.perform(get("/api/admin/dishes")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.dishes[0].id").value("D010"));
    }

    @Test
    void save_shouldRejectWhenDateInvalid() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(post("/api/admin/dishes")
                        .param("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"D001","name":"鱼香肉丝","price":18.5,"description":"desc","menuDate":"2099/01/02"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("供餐日须为 yyyy-MM-dd 格式"));

        verify(dishService, never()).saveDish(any());
    }

    @Test
    void save_shouldRejectWhenIdMissing() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(post("/api/admin/dishes")
                        .param("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":" ","name":"鱼香肉丝","price":18.5,"description":"desc","menuDate":"2099-01-02"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("请填写菜品编号"));
    }

    @Test
    void save_shouldRejectWhenNameMissing() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(post("/api/admin/dishes")
                        .param("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"D001","name":" ","price":18.5,"description":"desc","menuDate":"2099-01-02"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("请填写菜品名称"));
    }

    @Test
    void save_shouldRejectWhenPriceNegative() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(post("/api/admin/dishes")
                        .param("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"D001","name":"鱼香肉丝","price":-1,"description":"desc","menuDate":"2099-01-02"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("价格不能为负数"));
    }

    @Test
    void save_shouldTrimFieldsAndSucceed() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));
        when(dishService.saveDish(any(Dish.class))).thenReturn(true);

        mockMvc.perform(post("/api/admin/dishes")
                        .param("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":" D002 ","name":" 糖醋里脊 ","price":20.0,"description":null,"menuDate":" 2099-01-03 "}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("保存成功"))
                .andExpect(jsonPath("$.dish.id").value("D002"))
                .andExpect(jsonPath("$.dish.name").value("糖醋里脊"))
                .andExpect(jsonPath("$.dish.menuDate").value("2099-01-03"))
                .andExpect(jsonPath("$.dish.description").value(""));
    }

    @Test
    void save_shouldReturnFailureWhenServiceReturnsFalse() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));
        when(dishService.saveDish(any(Dish.class))).thenReturn(false);

        mockMvc.perform(post("/api/admin/dishes")
                        .param("username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id":"D007","name":"回锅肉","price":22.0,"description":"desc","menuDate":"2099-01-04"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("保存失败"));
    }

    @Test
    void delete_shouldRejectWhenDishHasOrders() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));
        when(orderDAO.countByDishId("D003")).thenReturn(2);

        mockMvc.perform(delete("/api/admin/dishes/D003")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("该菜品已有 2 条关联订单，无法删除。可修改供餐日或保留记录。"));

        verify(dishService, never()).deleteDishById(any());
    }

    @Test
    void delete_shouldRejectWhenIdMissing() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));

        mockMvc.perform(delete("/api/admin/dishes/ ")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缺少菜品编号"));
    }

    @Test
    void delete_shouldReturnSuccessWhenRemoved() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));
        when(orderDAO.countByDishId("D004")).thenReturn(0);
        when(dishService.deleteDishById("D004")).thenReturn(true);

        mockMvc.perform(delete("/api/admin/dishes/D004")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("已删除"));
    }

    @Test
    void delete_shouldReturnFailureWhenServiceReturnsFalse() throws Exception {
        when(userDAO.findByUsername("admin")).thenReturn(new User("admin", "123", "admin", "管理员"));
        when(orderDAO.countByDishId("D005")).thenReturn(0);
        when(dishService.deleteDishById("D005")).thenReturn(false);

        mockMvc.perform(delete("/api/admin/dishes/D005")
                        .param("username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("删除失败（可能菜品不存在）"));
    }
}
