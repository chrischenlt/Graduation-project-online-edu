package com.clt.service.edu.controller.api;

import com.clt.common.base.result.R;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author 陈力天
 * @Date 2022/2/3
 */
@CrossOrigin
@Api(description="讲师")
@RestController
@RequestMapping("/api/edu/teacher")
public class ApiTeacherController {

    @Autowired
    private TeacherService teacherService;

    @ApiOperation(value="所有讲师列表")
    @GetMapping("list")
    public R listAll(){
        List<Teacher> list = teacherService.list();
        return R.ok().data("items", list).message("获取讲师列表成功");
    }

    @ApiOperation(value = "获取讲师")
    @GetMapping("get/{id}")
    public R get(
            @ApiParam(value = "讲师ID", required = true)
            @PathVariable String id) {
        Map<String, Object> map = teacherService.selectTeacherInfoById(id);
        return R.ok().data(map);
    }
}