package com.clt.service.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.common.base.enums.BaseEnum;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.service.base.dto.CourseDto;
import com.clt.service.base.dto.MemberDto;
import com.clt.service.base.exception.MyException;
import com.clt.service.trade.entity.Order;
import com.clt.service.trade.enums.OrderEnum;
import com.clt.service.trade.feign.EduCourseService;
import com.clt.service.trade.feign.UcenterMemberService;
import com.clt.service.trade.mapper.OrderMapper;
import com.clt.service.trade.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clt.service.trade.util.OrderNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
    private UcenterMemberService ucenterMemberService;


    @Override
    public String saveOrder(String courseId, String memberId) {

        //查询当前用户是否已有当前课程的订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(OrderEnum.COURSE_ID.getColumn(), courseId);
        queryWrapper.eq(OrderEnum.MEMBER_ID.getColumn(), memberId);
        Order orderExist = baseMapper.selectOne(queryWrapper);
        if(orderExist != null){
            return orderExist.getId();//如果订单已存在，则直接返回订单id
        }

        //查询课程信息
        CourseDto courseDto = eduCourseService.getCourseDtoById(courseId);
        if(courseDto == null){
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }

        //查询用户信息
        MemberDto memberDto = ucenterMemberService.getMemberDtoByMemberId(memberId);
        if(memberDto == null){
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }

        //创建订单
        Order order = new Order();
        order.setOrderNo(OrderNoUtils.getOrderNo()); //订单号

        order.setCourseId(courseId);
        order.setCourseTitle(courseDto.getTitle());
        order.setCourseCover(courseDto.getCover());
        order.setTeacherName(courseDto.getTeacherName());
        order.setTotalFee(courseDto.getPrice().multiply(new BigDecimal(100)));//单位：分

        order.setMemberId(memberId);
        order.setMobile(memberDto.getMobile());
        order.setNickname(memberDto.getNickname());

        order.setStatus(0);//未支付
        order.setPayType(1); //微信支付

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

        Integer count = baseMapper.selectCount(queryWrapper);
        return count.intValue() > 0;
    }

}
