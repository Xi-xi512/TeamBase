package com.canteen.dao;


import com.canteen.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 订单数据访问接口
 * 负责订单数据的持久化操作
 */
@Mapper
public interface OrderDAO {
    /**
     * 读取所有订单数据
     * @return 订单列表
     */
    List<Order> readAllOrders();

    /**
     * 保存新订单
     * @param order 订单对象
     * @return 是否保存成功
     */
    boolean saveOrder(Order order);

    /**
     * 生成下一个订单 ID
     * @return 订单 ID
     */
    String generateOrderId();

    /**
     * 根据ID查找订单
     * @param orderId 订单ID
     * @return 订单对象
     */
    Order findById(String orderId);


}

