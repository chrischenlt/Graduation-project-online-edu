package com.clt.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 陈力天
 * @Date 2022/1/29
 */
@Data
public class CoursePublishVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String cover;
    private Integer lessonNum;
    private String subjectParentTitle;
    private String subjectTitle;
    private String teacherName;
    private String price;//只用于显示
}
