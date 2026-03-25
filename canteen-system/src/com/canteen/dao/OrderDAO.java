package com.canteen.dao;

import com.canteen.entity.Order;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问对象
 * 负责订单数据的文件读写操作
 */
public class OrderDAO {
    private static final String FILE_PATH = "data/orders.txt";

    /**
     * 读取所有订单数据
     * @return 订单列表
     */
    public List<Order> readAllOrders() {
        List<Order> orders = new ArrayList<>();
        File file = new File(FILE_PATH);
        
        // 如果文件不存在，返回空列表
        if (!file.exists()) {
            return orders;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            // 跳过标题行
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    Order order = new Order();
                    order.setOrderId(parts[0].trim());
                    order.setUsername(parts[1].trim());
                    order.setStudentName(parts[2].trim());
                    order.setDishId(parts[3].trim());
                    order.setDishName(parts[4].trim());
                    order.setPortion(parts[5].trim());
                    order.setPrice(Double.parseDouble(parts[6].trim()));
                    order.setDate(parts[7].trim());
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("读取订单文件失败：" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return orders;
    }

    /**
     * 保存新订单
     * @param order 订单对象
     * @return 是否保存成功
     */
    public boolean saveOrder(Order order) {
        List<Order> orders = readAllOrders();
        orders.add(order);
        return writeAllOrders(orders);
    }

    /**
     * 写入所有订单数据（全量覆盖）
     * @param orders 订单列表
     * @return 是否写入成功
     */
    public boolean writeAllOrders(List<Order> orders) {
        BufferedWriter writer = null;
        try {
            // 确保目录存在
            File file = new File(FILE_PATH);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            writer = new BufferedWriter(new FileWriter(file));
            
            // 写入标题行
            writer.write("订单 ID,账号，姓名，菜品 ID,菜品名称，份量，价格，日期");
            writer.newLine();
            
            // 写入订单数据
            for (Order order : orders) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%.2f,%s",
                        order.getOrderId(),
                        order.getUsername(),
                        order.getStudentName(),
                        order.getDishId(),
                        order.getDishName(),
                        order.getPortion(),
                        order.getPrice(),
                        order.getDate()));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("写入订单文件失败：" + e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成下一个订单 ID
     * @return 订单 ID
     */
    public String generateOrderId() {
        List<Order> orders = readAllOrders();
        int maxId = 0;
        for (Order order : orders) {
            try {
                int id = Integer.parseInt(order.getOrderId().replace("ORD", ""));
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // 忽略格式错误的 ID
            }
        }
        return "ORD" + String.format("%04d", maxId + 1);
    }
}
