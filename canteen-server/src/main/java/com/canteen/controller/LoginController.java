package com.canteen.controller;

import com.canteen.entity.User;
import com.canteen.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        User user = loginService.login(username, password);
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("success", true);
            response.put("user", user);
        } else {
            response.put("success", false);
            response.put("message", "账号或密码错误");
        }

        return response;
    }

    @GetMapping("/test-accounts")
    public Map<String, Object> getTestAccounts() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("accounts", loginService.getTestAccounts());
        return response;
    }
}
