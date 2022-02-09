package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/9
 */
public enum SubjectEnum {

    TITLE("title", "类别名称"),
    PARENT_ID("parent_id", "父ID"),
    SORT("sort", "排序字段"),
    ;

    private final String column;
    private final String label;

    SubjectEnum(String column, String label) {
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
