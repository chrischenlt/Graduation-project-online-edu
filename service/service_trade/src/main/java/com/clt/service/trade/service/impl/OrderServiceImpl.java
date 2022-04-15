package com.clt.service.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.common.base.enums.BaseEnum;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.service.base.dto.CourseDto;
import com.clt.service.base.dto.UserDto;
import com.clt.service.base.exception.MyException;
import com.clt.service.trade.entity.Order;
import com.clt.service.trade.entity.PayLog;
import com.clt.service.trade.enums.OrderEnum;
import com.clt.service.trade.feign.EduCourseService;
import com.clt.service.trade.feign.UcenterUserService;
import com.clt.service.trade.mapper.OrderMapper;
import com.clt.service.trade.mapper.PayLogMapper;
import com.clt.service.trade.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clt.service.trade.util.OrderNoUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

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

    @Autowired
    private EduCourseService eduCourseService;

    @Autowired
    private UcenterUserService ucenterUserService;

    @Autowired
    private PayLogMapper payLogMapper;


    @Override
    public String saveOrder(String courseId, String userId) {

        //查询当前用户是否已有当前课程的订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(OrderEnum.COURSE_ID.getColumn(), courseId);
        queryWrapper.eq(OrderEnum.MEMBER_ID.getColumn(), userId);
        Order orderExist = baseMapper.selectOne(queryWrapper);
        if(Objects.nonNull(orderExist)){
            return orderExist.getId();//如果订单已存在，则直接返回订单id
        }

        //查询课程信息
        CourseDto courseDto = eduCourseService.getCourseDtoById(courseId);
        if(Objects.isNull(courseDto)){
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }

        //查询用户信息
        UserDto userDto = ucenterUserService.getUserDtoByUserId(userId);
        if(Objects.isNull(userDto)){
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }

        //创建订单
        Order order = Order.builder()
                .orderNo(OrderNoUtils.getOrderNo())
                .courseId(courseId)
                .courseTitle(courseDto.getTitle())
                .courseCover(courseDto.getCover())
                .teacherName(courseDto.getTeacherName())
                .totalFee(courseDto.getPrice().multiply(new BigDecimal(100)))
                .memberId(userId)
                .mobile(userDto.getMobile())
                .nickname(userDto.getNickname())
                .status(0)  //未支付
                .payType(1) //微信支付
                .build();

        baseMapper.insert(order);
        return order.getId();
    }

    @Override
    public Order getByOrderId(String orderId, String memberId) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseEnum.ID.getColumn(), orderId).eq(OrderEnum.MEMBER_ID.getColumn(), memberId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean isBuyByCourseId(String courseId, String memberId) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(OrderEnum.COURSE_ID.getColumn(), courseId)
                .eq(OrderEnum.MEMBER_ID.getColumn(), memberId)
                .eq(OrderEnum.STATUS.getColumn(), 1);

        return baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<Order> selectByMemberId(String memberId) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .orderByDesc(BaseEnum.GMT_CREATE.getColumn())
                .eq(OrderEnum.MEMBER_ID.getColumn(), memberId);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public boolean removeById(String orderId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(BaseEnum.ID.getColumn(), orderId)
                .eq(OrderEnum.MEMBER_ID.getColumn(), memberId);
        return this.remove(queryWrapper);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(OrderEnum.ORDER_NO.getColumn(), orderNo);
        return baseMapper.selectOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(Map<String, String> notifyMap) {

        //更新订单状态
        String outTradeNo = notifyMap.get("out_trade_no");
        Order order = this.getOrderByOrderNo(outTradeNo);
        order.setStatus(1);//支付成功
        baseMapper.updateById(order);

        //记录支付日志
        PayLog payLog = new PayLog();
        payLog.setOrderNo(outTradeNo);
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型：微信支付
        payLog.setTotalFee(Long.parseLong(notifyMap.get("total_fee")));
        payLog.setTradeState(notifyMap.get("result_code"));
        payLog.setTransactionId(notifyMap.get("transaction_id"));
        payLog.setAttr(new Gson().toJson(notifyMap));
        payLogMapper.insert(payLog);

        //更新课程销量
        eduCourseService.updateBuyCountByCourseId(order.getCourseId());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(String orderNo) {

        //更新订单状态
        Order order = this.getOrderByOrderNo(orderNo);
        order.setStatus(1);//支付成功
        baseMapper.updateById(order);

        //记录支付日志
        PayLog payLog = new PayLog();
        payLog.setOrderNo(orderNo);
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型：微信支付
        payLog.setTotalFee(Long.parseLong(order.getTotalFee().toString().split("\\.")[0]));
        payLog.setTradeState("200");
        payLog.setTransactionId(orderNo);
        payLogMapper.insert(payLog);

        //更新课程销量
        eduCourseService.updateBuyCountByCourseId(order.getCourseId());
    }


    /**
     * 查询支付结果
     * @param orderNo
     * @return true 已支付  false 未支付
     */
    @Override
    public boolean queryPayStatus(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        Order order = baseMapper.selectOne(queryWrapper);
        return order.getStatus() == 1;
    }

    @Override
    public List<Order> getAllOrder() {
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.last("limit 10");
        List<Order> orderList = baseMapper.selectList(orderQueryWrapper);
        return orderList;
    }

    @Override
    public IPage<Order> selectPage(Page<Order> pageParam) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(BaseEnum.GMT_CREATE.getColumn());
        return baseMapper.selectPage(pageParam, queryWrapper);
    }
}
