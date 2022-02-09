package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/9
 */
public enum CourseDescriptionEnum {

    DESCRIPTION("description", "课程简介"),
    ;

    private final String column;
    private final String label;

    CourseDescriptionEnum(String column, String label) {
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
