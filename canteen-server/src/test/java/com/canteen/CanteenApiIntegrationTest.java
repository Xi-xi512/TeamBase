package com.canteen;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CanteenApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String base() {
        return "http://localhost:" + port;
    }

    // ── 1. 登录 ──────────────────────────────────────────────

    @Test
    @org.junit.jupiter.api.Order(1)
    void loginSuccess() {
        Map<String, String> body = Map.of("username", "student1", "password", "123456");
        ResponseEntity<Map> resp = restTemplate.postForEntity(base() + "/api/login", body, Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("user")).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void loginFailWrongPassword() {
        Map<String, String> body = Map.of("username", "student1", "password", "wrong");
        ResponseEntity<Map> resp = restTemplate.postForEntity(base() + "/api/login", body, Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testAccounts() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(base() + "/api/login/test-accounts", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("accounts")).isNotNull();
    }

    // ── 2. 菜品查询 ──────────────────────────────────────────

    @Test
    @org.junit.jupiter.api.Order(4)
    void getTomorrowMenu() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(base() + "/api/dishes", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("dishes")).isInstanceOf(List.class);
        assertThat(data.get("menuDate")).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void getDishById() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(base() + "/api/dishes/D001", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("dish")).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void getDishByIdNotFound() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(base() + "/api/dishes/NONEXIST", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    // ── 3. 下单 ──────────────────────────────────────────────

    private static String createdOrderId;

    @Test
    @org.junit.jupiter.api.Order(7)
    void createOrderSuccess() {
        Map<String, String> body = Map.of(
                "username", "student1",
                "dishId", "D001",
                "portion", "whole"
        );
        ResponseEntity<Map> resp = restTemplate.postForEntity(base() + "/api/orders", body, Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        Map<?, ?> order = (Map<?, ?>) data.get("order");
        assertThat(order).isNotNull();
        createdOrderId = (String) order.get("orderId");
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void createOrderMissingUsername() {
        Map<String, String> body = Map.of(
                "username", "",
                "dishId", "D001",
                "portion", "whole"
        );
        ResponseEntity<Map> resp = restTemplate.postForEntity(base() + "/api/orders", body, Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void createOrderInvalidPortion() {
        Map<String, String> body = Map.of(
                "username", "student1",
                "dishId", "D001",
                "portion", "quarter"
        );
        ResponseEntity<Map> resp = restTemplate.postForEntity(base() + "/api/orders", body, Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    // ── 4. 查询我的订单 ──────────────────────────────────────

    @Test
    @org.junit.jupiter.api.Order(10)
    void listMyOrdersSuccess() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                base() + "/api/orders/mine?username=student1", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("orders")).isInstanceOf(List.class);
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void listMyOrdersNotStudent() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                base() + "/api/orders/mine?username=admin", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    // ── 5. 更新份量 ──────────────────────────────────────────

    @Test
    @org.junit.jupiter.api.Order(12)
    void updatePortion() {
        // 需要已创建的订单 ID
        if (createdOrderId == null) return;
        Map<String, String> body = Map.of("portion", "half");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> resp = restTemplate.exchange(
                base() + "/api/orders/" + createdOrderId + "/portion",
                HttpMethod.PUT, request, Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
    }

    // ── 6. 取消订单 ──────────────────────────────────────────

    @Test
    @org.junit.jupiter.api.Order(13)
    void cancelOrderFailNotOwner() {
        ResponseEntity<Map> resp = restTemplate.exchange(
                base() + "/api/orders/NONEXIST?username=student2",
                HttpMethod.DELETE, null, Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    // ── 7. 管理员统计 ────────────────────────────────────────

    @Test
    @org.junit.jupiter.api.Order(14)
    void statisticsByAdmin() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                base() + "/api/orders/statistics?username=admin", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("statistics")).isNotNull();
    }

    @Test
    @org.junit.jupiter.api.Order(15)
    void statisticsByStudentForbidden() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                base() + "/api/orders/statistics?username=student1", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    // ── 8. 管理员菜品管理 ────────────────────────────────────

    @Test
    @org.junit.jupiter.api.Order(16)
    void adminListDishes() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                base() + "/api/admin/dishes?username=admin", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(true);
        assertThat(data.get("dishes")).isInstanceOf(List.class);
    }

    @Test
    @org.junit.jupiter.api.Order(17)
    void adminListDishesUnauthorized() {
        ResponseEntity<Map> resp = restTemplate.getForEntity(
                base() + "/api/admin/dishes?username=student1", Map.class);
        Map<?, ?> data = resp.getBody();
        assertThat(data.get("success")).isEqualTo(false);
    }

    @Test
    @org.junit.jupiter.api.Order(18)
    void adminSaveAndDeleteDish() {
        // 新增菜品
        Map<String, Object> dish = Map.of(
                "id", "ITEST01",
                "name", "集成测试菜品",
                "price", 18.0,
                "description", "测试描述",
                "menuDate", "2026-12-31"
        );
        ResponseEntity<Map> saveResp = restTemplate.postForEntity(
                base() + "/api/admin/dishes?username=admin", dish, Map.class);
        assertThat(saveResp.getBody().get("success")).isEqualTo(true);

        // 删除（无关联订单，应成功）
        ResponseEntity<Map> delResp = restTemplate.exchange(
                base() + "/api/admin/dishes/ITEST01?username=admin",
                HttpMethod.DELETE, null, Map.class);
        assertThat(delResp.getBody().get("success")).isEqualTo(true);
    }
}
