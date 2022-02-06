package com.clt.service.cms.feign.fallback;

import com.clt.common.base.result.R;
import com.clt.service.cms.feign.OssFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author 陈力天
 * @Date 2022/2/4
 */
@Service
@Slf4j
public class OssFileServiceFallBack implements OssFileService {

    @Override
    public R removeFile(String url) {
        log.error("远程服务异常");
        return R.error().message("调用超时");
    }
}
