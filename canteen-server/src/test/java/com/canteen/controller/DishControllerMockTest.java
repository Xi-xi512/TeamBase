package com.canteen.controller;

import com.canteen.entity.Dish;
import com.canteen.service.DishService;
import com.canteen.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DishController.class)
class DishControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DishService dishService;

    @Test
    void getTomorrowDishes_shouldReturnListAndTomorrowDate() throws Exception {
        String tomorrow = DateUtils.tomorrowYyyyMmDd();
        when(dishService.getTomorrowMenu()).thenReturn(List.of(
                new Dish("D001", "宫保鸡丁", 16.0, "desc", tomorrow)
        ));

        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.dishes.length()").value(1))
                .andExpect(jsonPath("$.menuDate").value(tomorrow));
    }

    @Test
    void getDishById_shouldReturnFailureWhenNotFound() throws Exception {
        when(dishService.getDishById("D002")).thenReturn(null);

        mockMvc.perform(get("/api/dishes/D002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("菜品不存在"));
    }

    @Test
    void getDishById_shouldReturnDishWhenFound() throws Exception {
        when(dishService.getDishById("D003")).thenReturn(
                new Dish("D003", "鱼香肉丝", 18.5, "desc", "2099-01-02")
        );

        mockMvc.perform(get("/api/dishes/D003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.dish.id").value("D003"))
                .andExpect(jsonPath("$.dish.name").value("鱼香肉丝"));
    }
}
