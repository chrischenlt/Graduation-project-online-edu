package com.clt.service.trade.feign.fallback;

import com.clt.common.base.result.R;
import com.clt.service.base.dto.CourseDto;
import com.clt.service.trade.feign.EduCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
@Service
@Slf4j
public class EduCourseServiceFallback implements EduCourseService {

    @Override
    public CourseDto getCourseDtoById(String courseId) {
        log.error("远程服务异常");
        return null;
    }

    @Override
    public R updateBuyCountById(String id) {
        log.error("远程服务异常");
        return null;
    }


}
