package com.clt.service.trade.service.impl;

import com.clt.service.trade.entity.PayLog;
import com.clt.service.trade.mapper.PayLogMapper;
import com.clt.service.trade.service.PayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-02-08
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

}
