package com.clt.service.trade.controller.api;


import com.clt.common.base.result.R;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.JwtInfo;
import com.clt.common.base.util.JwtUtils;
import com.clt.service.trade.entity.Order;
import com.clt.service.trade.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author chenlt
 * @since 2022-02-08
 */
@RestController
@RequestMapping("/api/trade/order")
@Api(description = "网站订单管理")
@Slf4j
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("新增订单")
    @PostMapping("auth/save/{courseId}")
    public R save(@PathVariable String courseId, HttpServletRequest request){

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        String orderId = orderService.saveOrder(courseId, jwtInfo.getId());
        return R.ok().data("orderId", orderId);
    }

    @ApiOperation("获取订单")
    @GetMapping("auth/get/{orderId}")
    public R get(@PathVariable String orderId, HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        Order order = orderService.getByOrderId(orderId, jwtInfo.getId());
        return R.ok().data("item", order);
    }

    @ApiOperation( "判断课程是否购买")
    @GetMapping("auth/is-buy/{courseId}")
    public R isBuyByCourseId(@PathVariable String courseId, HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        Boolean isBuy = orderService.isBuyByCourseId(courseId, jwtInfo.getId());
        return R.ok().data("isBuy", isBuy);
    }

    @ApiOperation(value = "获取当前用户订单列表")
    @GetMapping("auth/list")
    public R list(HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        List<Order> list = orderService.selectByMemberId(jwtInfo.getId());
        return R.ok().data("items", list);
    }

    @ApiOperation(value = "删除订单")
    @DeleteMapping("auth/remove/{orderId}")
    public R remove(@PathVariable String orderId, HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        boolean result = orderService.removeById(orderId, jwtInfo.getId());
        if(result){
            return R.ok().message("删除成功");
        }
        return R.error().message("数据不存在");
    }

    @GetMapping("query-pay-status/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        boolean result = orderService.queryPayStatus(orderNo);
        if (result) {//支付成功
            return R.ok().message("支付成功");
        }
        return R.setResult(ResultCodeEnum.PAY_RUN);//支付中
    }

    @GetMapping("finish/order/pay/{orderNo}")
    public R finishOrderPay(@PathVariable String orderNo) {
        orderService.updateOrderStatus(orderNo);
        return R.ok().message("支付成功");
    }

}

