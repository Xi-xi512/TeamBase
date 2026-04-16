package com.canteen.entity;


/**
 * 订单实体类
 * 用于存储学生订餐信息
 */
public class Order {
    private String orderId;         // 订单 ID
    private String username;        // 订餐学生账号
    private String studentName;     // 学生姓名
    private String dishId;          // 菜品 ID
    private String dishName;        // 菜品名称
    private String portion;         // 份量：whole(整份) 或 half(半份)
    private double price;           // 订单价格
    private String date;            // 预定日期（默认为明日）

    public Order() {
    }

    public Order(String orderId, String username, String studentName,
                 String dishId, String dishName, String portion, double price, String date) {
        this.orderId = orderId;
        this.username = username;
        this.studentName = studentName;
        this.dishId = dishId;
        this.dishName = dishName;
        this.portion = portion;
        this.price = price;
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getPortion() {
        return portion;
    }

    public void setPortion(String portion) {
        this.portion = portion;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", username='" + username + '\'' +
                ", studentName='" + studentName + '\'' +
                ", dishId='" + dishId + '\'' +
                ", dishName='" + dishName + '\'' +
                ", portion='" + portion + '\'' +
                ", price=" + price +
                ", date='" + date + '\'' +
                '}';
    }
}
