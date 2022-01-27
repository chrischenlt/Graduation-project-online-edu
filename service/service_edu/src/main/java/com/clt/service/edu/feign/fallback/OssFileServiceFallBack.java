package com.clt.service.edu.feign.fallback;

import com.clt.common.base.result.R;
import com.clt.service.edu.feign.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author 陈力天
 * @Date 2022/1/19
 */
@Service
@Slf4j
public class OssFileServiceFallBack implements OssFileService {
    @Override
    public R removeFile(String url) {
        log.error("远程服务异常");
        return R.error();
    }
}
