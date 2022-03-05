package com.clt.service.trade.feign.fallback;

import com.clt.service.base.dto.UserDto;
import com.clt.service.trade.feign.UcenterUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
@Service
@Slf4j
public class UcenterUserServiceFallback implements UcenterUserService {
    @Override
    public UserDto getUserDtoByUserId(String UserId) {
        log.error("远程服务异常");
        return null;
    }
}
