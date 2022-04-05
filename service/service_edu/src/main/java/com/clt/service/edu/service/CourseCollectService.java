package com.clt.service.edu.service;

import com.clt.service.edu.entity.CourseCollect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clt.service.edu.entity.vo.CourseCollectVo;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
public interface CourseCollectService extends IService<CourseCollect> {

    boolean isCollect(String courseId, String userId);

    List<CourseCollectVo> selectCourseCollectListByUserId(String memberId);

    void insertOrUpdateCourseLike(String courseId, String isLike, String userId);

    void insertOrUpdateCourseCollect(String courseId, String isCollect, String userId);

    String getCourseLikeCount(String courseId);

    String getCourseCollectCount(String courseId);

    boolean isLike(String courseId, String userId);

    String getCourseBuyCount(String courseId);
}
