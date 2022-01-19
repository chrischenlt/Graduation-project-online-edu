package com.clt.service.edu.feign;

import com.clt.common.base.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author 陈力天
 * @Date 2022/1/17
 */
@FeignClient("service-oss")
public interface OssFileService {

    @DeleteMapping("/admin/oss/file/remove")
    R removeFile(@RequestBody String url);


}
