package com.clt.service.trade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.service.trade.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author chenlt
 * @since 2022-02-08
 */
public interface OrderService extends IService<Order> {

    String saveOrder(String courseId, String id);

    Order getByOrderId(String orderId, String memberId);

    Boolean isBuyByCourseId(String courseId, String memberId);

    List<Order> selectByMemberId(String id);

    boolean removeById(String orderId, String memberId);

    Order getOrderByOrderNo(String orderNo);

    void updateOrderStatus(Map<String, String> notifyMap);

    void updateOrderStatus(String orderNo);

    boolean queryPayStatus(String orderNo);

    List<Order> getAllOrder();

    IPage<Order> selectPage(Page<Order> pageParam);
}
