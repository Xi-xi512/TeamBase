package com.canteen.controller;

import com.canteen.entity.User;
import com.canteen.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
class LoginControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @Test
    void login_shouldReturnFailureWhenCredentialInvalid() throws Exception {
        when(loginService.login("stu1", "bad")).thenReturn(null);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","password":"bad"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("账号或密码错误"));
    }

    @Test
    void login_shouldReturnUserWhenCredentialValid() throws Exception {
        when(loginService.login("stu1", "123")).thenReturn(new User("stu1", "123", "student", "张三"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"stu1","password":"123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.username").value("stu1"))
                .andExpect(jsonPath("$.user.role").value("student"));
    }

    @Test
    void getTestAccounts_shouldReturnAccounts() throws Exception {
        when(loginService.getTestAccounts()).thenReturn("账号：stu1, 密码：123");

        mockMvc.perform(get("/api/login/test-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accounts").value("账号：stu1, 密码：123"));
    }
}
