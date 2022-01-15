package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/1/8
 */
public enum TeacherEnum {

    NAME("name", "讲师姓名"),
    INTRO("intro", "讲师简介"),
    CAREER("career", "讲师资历,一句话说明讲师"),
    LEVEL("level", "头衔 1高级讲师 2首席讲师"),
    AVATAR("avatar", "讲师头像"),
    SORT("sort", "排序"),
    JOIN_DATE("joinDate", "入驻时间"),
    IS_DELETED("isDeleted", "逻辑删除 1（true）已删除， 0（false）未删除"),
    ;


    private final String column;
    private final String label;

    TeacherEnum(String column, String label) {
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
