package com.clt.service.edu.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clt.common.base.result.R;
import com.clt.service.edu.entity.form.CourseInfoForm;
import com.clt.service.edu.entity.vo.CoursePublishVo;
import com.clt.service.edu.entity.vo.CourseQueryVo;
import com.clt.service.edu.entity.vo.CourseVo;
import com.clt.service.edu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@CrossOrigin
@Api(description = "课程管理")
@RestController
@RequestMapping("/admin/edu/course")
@Slf4j
public class CourseController {

    @Resource
    private CourseService courseService;

    @ApiOperation("新增课程")
    @PostMapping("save-course-info")
    public R saveCourseInfo(
            @ApiParam(value = "课程基本信息", required = true)
            @RequestBody CourseInfoForm courseInfoForm) {
        String courseId = courseService.saveCourseInfo(courseInfoForm);
        return R.ok().data("courseId", courseId).message("保存成功");
    }



    @ApiOperation("根据ID查询课程")
    @GetMapping("course-info/{id}")
    public R getCourseById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id) {

        if (StringUtils.isEmpty(id)) {
            return R.ok().message("数据不存在");
        }

        CourseInfoForm courseInfoForm = courseService.getCourseInfoById(id);

        if (Objects.nonNull(courseInfoForm)) {
            return R.ok().data("item", courseInfoForm);
        }

        return R.ok().message("数据不存在");
    }


    @ApiOperation("更新课程")
    @PutMapping("update-course-info")
    public R updateCourseById(@ApiParam(value = "课程Id", required = true)
                              @RequestBody CourseInfoForm courseInfoForm) {

        courseService.updateCourseInfoById(courseInfoForm);

        return R.ok().message("数据更新成功");
    }

    @ApiOperation("课程分页列表")
    @GetMapping("list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit,
                      @ApiParam("课程列表查询对象") CourseQueryVo courseQueryVo){

        IPage<CourseVo> pageModel = courseService.selectPage(page, limit, courseQueryVo);
        List<CourseVo> records = pageModel.getRecords();
        long total = pageModel.getTotal();
        return R.ok().data("total", total).data("rows", records);
    }


    @ApiOperation("根据Id删除课程")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "课程Id" ,required = true) @PathVariable String id) {

        if (!courseService.removeCoverById(id)) {
            log.error("oss课程封面图片删除失败, id : {}", id);
        }

        if (courseService.removeCourseById(id)) {
            return R.ok().message("删除成功");
        }
        return R.ok().message("删除失败");
    }


    @ApiOperation("根据ID获取课程发布信息")
    @GetMapping("course-publish/{id}")
    public R getCoursePublishVoById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id){

        CoursePublishVo coursePublishVo = courseService.getCoursePublishVoById(id);
        if (coursePublishVo != null) {
            return R.ok().data("item", coursePublishVo);
        } else {
            return R.error().message("数据不存在");
        }
    }

    @ApiOperation("根据id发布课程")
    @PutMapping("publish-course/{id}")
    public R publishCourseById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String id){

        if (courseService.publishCourseById(id)) {
            return R.ok().message("发布成功");
        }
        return R.error().message("数据不存在");
    }

}

