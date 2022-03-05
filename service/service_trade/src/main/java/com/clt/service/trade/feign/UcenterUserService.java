package com.clt.service.trade.feign;

import com.clt.service.base.dto.UserDto;
import com.clt.service.trade.feign.fallback.UcenterUserServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
@Service
@FeignClient(value = "service-ucenter", fallback = UcenterUserServiceFallback.class)
public interface UcenterUserService {

    @GetMapping("/api/ucenter/member/inner/get-member-dto/{memberId}")
    UserDto getUserDtoByUserId(@PathVariable(value = "memberId") String userId);
}
