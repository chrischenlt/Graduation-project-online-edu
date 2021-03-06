package com.clt.service.statistics.feign;

import com.clt.common.base.result.R;
import com.clt.service.statistics.feign.fallback.UcenterUserServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author 陈力天
 * @Date 2022/2/14
 */
@FeignClient(value = "service-ucenter", fallback = UcenterUserServiceFallBack.class)
public interface UcenterUserService {

    @GetMapping("/admin/ucenter/member/count-register-num/{day}")
    R countRegisterNum(@PathVariable("day") String day);
}
