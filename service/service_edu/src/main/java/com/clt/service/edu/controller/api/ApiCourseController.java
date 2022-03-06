package com.clt.service.edu.controller.api;

import com.clt.common.base.result.R;
import com.clt.common.base.util.JwtInfo;
import com.clt.common.base.util.JwtUtils;
import com.clt.service.base.dto.CourseDto;
import com.clt.service.edu.entity.Course;
import com.clt.service.edu.entity.vo.ChapterVo;
import com.clt.service.edu.entity.vo.CourseCollectVo;
import com.clt.service.edu.entity.vo.WebCourseQueryVo;
import com.clt.service.edu.entity.vo.WebCourseVo;
import com.clt.service.edu.service.ChapterService;
import com.clt.service.edu.service.CourseCollectService;
import com.clt.service.edu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @Author 陈力天
 * @Date 2022/2/3
 */

@Api(description="课程")
@RestController
@RequestMapping("/api/edu/course")
@Slf4j
public class ApiCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CourseCollectService courseCollectService;

    @ApiOperation("课程列表")
    @GetMapping("list")
    public R pageList(
            @ApiParam(value = "查询对象", required = true)
                    WebCourseQueryVo webCourseQueryVo){

        List<Course> courseList = courseService.webSelectList(webCourseQueryVo);

        return R.ok().data("courseList", courseList);
    }

    @ApiOperation("根据id查询课程")
    @GetMapping("get/{courseId}")
    public R getById(
            @ApiParam(value = "课程id", required = true)
            @PathVariable String courseId,
            HttpServletRequest request){

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);

        //查询课程信息和讲师信息
        WebCourseVo webCourseVo = courseService.selectWebCourseVoById(courseId, Objects.isNull(jwtInfo) ? null : jwtInfo.getId());

        //查询当前课程的嵌套章节和课时信息
        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);
        return R.ok().data("course", webCourseVo).data("chapterVoList", chapterVoList);
    }

    @ApiOperation("根据课程id查询课程信息")
    @GetMapping("inner/get-course-dto/{courseId}")
    public CourseDto getCourseDtoById(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String courseId){
        CourseDto courseDto = courseService.getCourseDtoById(courseId);
        return courseDto;
    }

    @ApiOperation("根据课程id更改销售量")
    @GetMapping("inner/update-buy-count/{courseId}")
    public R updateBuyCountById(
            @ApiParam(value = "课程id", required = true)
            @PathVariable String courseId){
        courseService.updateBuyCountByCourseId(courseId);
        return R.ok();
    }


    @ApiOperation(value = "判断是否收藏")
    @GetMapping("auth/is-collect/{courseId}")
    public R isCollect(
            @ApiParam(name = "courseId", value = "课程id", required = true)
            @PathVariable String courseId,
            HttpServletRequest request) {

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        boolean isCollect = courseCollectService.isCollect(courseId, jwtInfo.getId());
        return R.ok().data("isCollect", isCollect);
    }


    @ApiOperation(value = "获取课程收藏列表")
    @GetMapping("auth/get/collect/list")
    public R collectList(HttpServletRequest request) {

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        List<CourseCollectVo> list = courseCollectService.selectCourseCollectListByUserId(jwtInfo.getId());
        return R.ok().data("items", list);
    }


    @ApiOperation(value = "更新收藏课程状态")
    @PostMapping("auth/add/collect/{courseId}/{isCourse}")
    public R insertOrUpdateCollectByCourseId(
            @ApiParam(value = "课程id", required = true)
            @PathVariable String courseId,
            @PathVariable String isCourse,
            HttpServletRequest request) {

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        courseCollectService.insertOrUpdateCourseCollect(courseId, isCourse, jwtInfo.getId());
        return R.ok().message("收藏状态更新成功");
    }


    @ApiOperation(value = "获取对应课程点赞数")
    @GetMapping("get/collect/like/count/{courseId}")
    public R getLikeCountByCourse(
            @ApiParam(name = "courseId", value = "课程id", required = true)
            @PathVariable String courseId) {

        return R.ok().message(courseCollectService.getCourseLikeCount(courseId));
    }


    @ApiOperation(value = "更新点赞课程状态")
    @PostMapping("auth/add/like/{courseId}/{isLike}")
    public R insertOrUpdateLikeByCourseId(
            @ApiParam(value = "课程id", required = true)
            @PathVariable String courseId,
            @PathVariable String isLike,
            HttpServletRequest request) {

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        courseCollectService.insertOrUpdateCourseLike(courseId, isLike, jwtInfo.getId());
        return R.ok().message("点赞状态更新成功");
    }


    @ApiOperation(value = "判断是否点赞")
    @GetMapping("auth/is-like/{courseId}")
    public R isLike(
            @ApiParam(name = "courseId", value = "课程id", required = true)
            @PathVariable String courseId,
            HttpServletRequest request) {

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
        boolean isLike = courseCollectService.isLike(courseId, jwtInfo.getId());
        return R.ok().data("isLike", isLike);
    }



}
