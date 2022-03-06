package com.clt.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 陈力天
 * @Date 2022/2/11
 */
@Data
public class CourseCollectVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id; //课程id
//    private String courseId; //课程id
    private String title;//标题
    private BigDecimal price;//价格
    private Integer lessonNum;//课时数
    private String cover;//封面
    private String gmtCreate;//收藏时间
    private String teacherName;//讲师
}
