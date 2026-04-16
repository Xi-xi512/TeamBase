package com.canteen.controller;

import com.canteen.entity.Dish;
import com.canteen.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping
    public Map<String, Object> getAllDishes() {
        List<Dish> dishes = dishService.getAllDishes();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("dishes", dishes);
        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDishById(@PathVariable String id) {
        Dish dish = dishService.getDishById(id);
        Map<String, Object> response = new HashMap<>();

        if (dish != null) {
            response.put("success", true);
            response.put("dish", dish);
        } else {
            response.put("success", false);
            response.put("message", "菜品不存在");
        }

        return response;
    }
}
