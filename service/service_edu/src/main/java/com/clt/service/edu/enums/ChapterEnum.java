package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/9
 */
public enum ChapterEnum {

    COURSE_ID("course_id", "课程ID"),
    TITLE("title","节点名称"),
    SORT("sort","排序字段"),
    ;


    private final String column;
    private final String label;

    ChapterEnum(String column, String label) {
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
