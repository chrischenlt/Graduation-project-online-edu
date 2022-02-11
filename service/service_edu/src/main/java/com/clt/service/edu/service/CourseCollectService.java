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

    boolean isCollect(String courseId, String memberId);

    void saveCourseCollect(String courseId, String memberId);

    List<CourseCollectVo> selectListByMemberId(String memberId);

    boolean removeCourseCollect(String courseId, String memberId);
}
