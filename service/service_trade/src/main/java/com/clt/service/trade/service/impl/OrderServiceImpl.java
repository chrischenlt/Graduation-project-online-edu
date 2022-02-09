package com.clt.service.trade.service.impl;

import com.clt.service.trade.entity.Order;
import com.clt.service.trade.mapper.OrderMapper;
import com.clt.service.trade.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-02-08
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
