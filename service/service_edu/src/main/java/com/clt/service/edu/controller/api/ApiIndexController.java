package com.clt.service.edu.controller.api;

import com.clt.common.base.result.R;
import com.clt.service.edu.entity.Course;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.service.CourseService;
import com.clt.service.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/2/5
 */

@Api(description="首页")
@RestController
@RequestMapping("/api/edu/index")
public class ApiIndexController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherService teacherService;

    @ApiOperation("课程和讲师的首页数据")
    @GetMapping
    public R index(){

        //查询热门课程
        List<Course> courseList = courseService.selectHotCourse();

        //查询推荐讲师
        List<Teacher> teacherList = teacherService.selectHotTeacher();

        return R.ok().data("courseList", courseList).data("teacherList", teacherList);
    }
}