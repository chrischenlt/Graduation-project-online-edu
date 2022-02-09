package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/9
 */
public enum CourseCollectEnum {

    COURSE_ID("course_id", "课程ID"),
    MEMBER_ID("member_id", "会员id"),
    ;

    private final String column;
    private final String label;

    CourseCollectEnum(String column, String label) {
        this.column = column;
        this.label = label;
    }

    public String getColumn() {
        return column;
    }

    public String getLabel() {
        return label;
    }
}
