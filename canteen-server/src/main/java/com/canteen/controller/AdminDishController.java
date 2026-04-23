package com.canteen.controller;

import com.canteen.dao.OrderDAO;
import com.canteen.dao.UserDAO;
import com.canteen.entity.Dish;
import com.canteen.entity.User;
import com.canteen.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 管理员：每日菜品维护（增删改查）
 */
@RestController
@RequestMapping("/api/admin/dishes")
public class AdminDishController {

    private static final Pattern DATE_YMD = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DishService dishService;

    @Autowired
    private OrderDAO orderDAO;

    private boolean isAdmin(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        User u = userDAO.findByUsername(username.trim());
        return u != null && "admin".equals(u.getRole());
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestParam String username,
            @RequestParam(required = false) String menuDate) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(username)) {
            response.put("success", false);
            response.put("message", "无权限管理菜品");
            return response;
        }
        List<Dish> list;
        if (menuDate != null && !menuDate.trim().isEmpty()) {
            list = dishService.listByMenuDate(menuDate.trim());
        } else {
            list = dishService.getAllDishes();
        }
        response.put("success", true);
        response.put("dishes", list);
        return response;
    }

    @PostMapping
    public Map<String, Object> save(@RequestParam String username, @RequestBody Dish dish) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(username)) {
            response.put("success", false);
            response.put("message", "无权限管理菜品");
            return response;
        }
        if (dish.getId() == null || dish.getId().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请填写菜品编号");
            return response;
        }
        if (dish.getName() == null || dish.getName().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "请填写菜品名称");
            return response;
        }
        if (dish.getMenuDate() == null || !DATE_YMD.matcher(dish.getMenuDate().trim()).matches()) {
            response.put("success", false);
            response.put("message", "供餐日须为 yyyy-MM-dd 格式");
            return response;
        }
        if (dish.getPrice() < 0) {
            response.put("success", false);
            response.put("message", "价格不能为负数");
            return response;
        }
        dish.setId(dish.getId().trim());
        dish.setName(dish.getName().trim());
        dish.setMenuDate(dish.getMenuDate().trim());
        if (dish.getDescription() == null) {
            dish.setDescription("");
        }

        boolean ok = dishService.saveDish(dish);
        if (ok) {
            response.put("success", true);
            response.put("message", "保存成功");
            response.put("dish", dish);
        } else {
            response.put("success", false);
            response.put("message", "保存失败");
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(
            @RequestParam String username,
            @PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(username)) {
            response.put("success", false);
            response.put("message", "无权限管理菜品");
            return response;
        }
        if (id == null || id.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "缺少菜品编号");
            return response;
        }
        int n = orderDAO.countByDishId(id.trim());
        if (n > 0) {
            response.put("success", false);
            response.put("message", "该菜品已有 " + n + " 条关联订单，无法删除。可修改供餐日或保留记录。");
            return response;
        }
        boolean ok = dishService.deleteDishById(id.trim());
        if (ok) {
            response.put("success", true);
            response.put("message", "已删除");
        } else {
            response.put("success", false);
            response.put("message", "删除失败（可能菜品不存在）");
        }
        return response;
    }
}
