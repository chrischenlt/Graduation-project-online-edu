package com.clt.service.statistics.feign.fallback;

import com.clt.common.base.result.R;
import com.clt.service.statistics.feign.UcenterUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UcenterUserServiceFallBack implements UcenterUserService {

    @Override
    public R countRegisterNum(String day) {
        log.error("远程服务异常");
        return R.ok().data("registerNum", 0);
    }
}
