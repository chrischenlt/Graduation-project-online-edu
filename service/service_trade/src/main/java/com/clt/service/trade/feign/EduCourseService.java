package com.clt.service.trade.feign;

import com.clt.common.base.result.R;
import com.clt.service.base.dto.CourseDto;
import com.clt.service.trade.feign.fallback.EduCourseServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
@Service
@FeignClient(value = "service-edu", fallback = EduCourseServiceFallback.class)
public interface EduCourseService {

    @GetMapping("/api/edu/course/inner/get-course-dto/{courseId}")
    CourseDto getCourseDtoById(@PathVariable(value = "courseId") String courseId);

    @GetMapping("/api/edu/course/inner/update-buy-count/{id}")
    R updateBuyCountById(@PathVariable("id") String id);
}

