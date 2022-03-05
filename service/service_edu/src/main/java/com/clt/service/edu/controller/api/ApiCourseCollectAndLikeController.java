//package com.clt.service.edu.controller.api;
//
//import com.clt.common.base.result.R;
//import com.clt.common.base.util.JwtInfo;
//import com.clt.common.base.util.JwtUtils;
//import com.clt.service.edu.entity.vo.CourseCollectVo;
//import com.clt.service.edu.service.CourseCollectService;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
///**
// * @Author 陈力天
// * @Date 2022/2/11
// */
//
//@RestController
//@RequestMapping("/api/edu/course-collect-or-like")
//@Slf4j
//public class ApiCourseCollectAndLikeController {
//    @Autowired
//    private CourseCollectService courseCollectService;
//
//    @ApiOperation(value = "判断是否收藏")
//    @GetMapping("auth/is-collect/{courseId}")
//    public R isCollect(
//            @ApiParam(name = "courseId", value = "课程id", required = true)
//            @PathVariable String courseId,
//            HttpServletRequest request) {
//
//        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
//        boolean isCollect = courseCollectService.isCollect(courseId, jwtInfo.getId());
//        return R.ok().data("isCollect", isCollect);
//    }
//
//
//    @ApiOperation(value = "获取课程收藏列表")
//    @GetMapping("auth/collect/list")
//    public R collectList(HttpServletRequest request) {
//
//        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
//        List<CourseCollectVo> list = courseCollectService.selectListByUserId(jwtInfo.getId());
//        return R.ok().data("items", list);
//    }
//
//
//    @ApiOperation(value = "收藏课程")
//    @PostMapping("auth/save/collect/{courseId}")
//    public R collectCourse(
//            @ApiParam(name = "courseId", value = "课程id", required = true)
//            @PathVariable String courseId,
//            HttpServletRequest request) {
//
//        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
//        courseCollectService.saveCourseCollect(courseId, jwtInfo.getId());
//        return R.ok();
//    }
//
//
//    @ApiOperation(value = "取消收藏课程")
//    @PostMapping("auth/remove/collect/{courseId}")
//    public R removeCourseCollect(
//            @ApiParam(name = "courseId", value = "课程id", required = true)
//            @PathVariable String courseId,
//            HttpServletRequest request) {
//
//        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
//        boolean result = courseCollectService.removeCourseCollect(courseId, jwtInfo.getId());
//        if (result) {
//            return R.ok().message("取消收藏成功");
//        }
//        return R.error().message("取消收藏失败");
//    }
//
//
//    @ApiOperation(value = "获取对应课程点赞数")
//    @GetMapping("get/collect/like/count/{courseId}")
//    public R getLikeCountByCourse(
//            @ApiParam(name = "courseId", value = "课程id", required = true)
//            @PathVariable String courseId) {
//
//        return R.ok().message(courseCollectService.getCourseLikeCount(courseId));
//    }
//
//
//    @ApiOperation(value = "更新点赞课程状态")
//    @PostMapping("auth/add/like/{courseId}/{like}")
//    public R insertOrUpdateLikeByCourseId(
//            @ApiParam(value = "课程id", required = true)
//            @PathVariable String courseId,
//            @PathVariable String like,
//            HttpServletRequest request) {
//
//        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
//        courseCollectService.insertOrUpdateCourseLike(courseId, like, jwtInfo.getId());
//        return R.ok().message("点赞状态更新成功");
//    }
//
//
//    @ApiOperation(value = "判断是否点赞")
//    @GetMapping("auth/is-like/{courseId}")
//    public R isLike(
//            @ApiParam(name = "courseId", value = "课程id", required = true)
//            @PathVariable String courseId,
//            HttpServletRequest request) {
//
//        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);
//        boolean isLike = courseCollectService.isLike(courseId, jwtInfo.getId());
//        return R.ok().data("isLike", isLike);
//    }
//
//
//}
