package com.clt.service.edu.feign.fallback;

import com.clt.common.base.result.R;
import com.clt.service.edu.feign.VodMediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/2/2
 */
@Service
@Slf4j
public class VodMediaServiceFallBack implements VodMediaService {
    @Override
    public R removeVideo(String vodId) {
        log.error("远程服务异常");
        return R.error();
    }

    @Override
    public R removeVideoByIdList(List<String> videoIdList) {
        log.error("远程服务异常");
        return R.error();
    }
}
