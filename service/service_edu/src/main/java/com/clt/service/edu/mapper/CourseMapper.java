package com.clt.service.edu.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.service.base.dto.CourseDto;
import com.clt.service.edu.entity.Course;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clt.service.edu.entity.form.CourseInfoForm;
import com.clt.service.edu.entity.vo.CoursePublishVo;
import com.clt.service.edu.entity.vo.CourseVo;
import com.clt.service.edu.entity.vo.WebCourseVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@Repository
public interface CourseMapper extends BaseMapper<Course> {

    @Select("SELECT * FROM `edu_course` ec, `edu_course_description` ecd " +
            "where ec.id = #{id} and ecd.id = #{id}")
    CourseInfoForm getCourseInfoById(@Param("id") String id);

    List<CourseVo> selectPageByCourseQueryVo(
            Page<CourseVo> pageParam,
            @Param(Constants.WRAPPER) QueryWrapper<CourseVo> queryWrapper);

    CoursePublishVo selectCoursePublishVoById(String id);

    WebCourseVo selectWebCourseVoById(String courseId);

    CourseDto selectCourseDtoById(String courseId);
}
