package com.canteen.entity;



/**
 * 菜品实体类
 * 用于存储菜品信息
 */
public class Dish {
    private String id;            // 菜品 ID
    private String name;          // 菜品名称
    private double price;         // 价格（整份）
    private String description;   // 菜品描述
    /** 供餐/可订日期 yyyy-MM-dd，与订单 date 一致（明日订餐即明日） */
    private String menuDate;

    public Dish() {
    }

    public Dish(String id, String name, double price, String description, String menuDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.menuDate = menuDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(String menuDate) {
        this.menuDate = menuDate;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", menuDate='" + menuDate + '\'' +
                '}';
    }
}

