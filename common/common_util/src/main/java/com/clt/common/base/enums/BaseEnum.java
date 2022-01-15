package com.clt.common.base.enums;

/**
 * @Author 陈力天
 * @Date 2022/1/8
 */
public enum BaseEnum {

    ID("id", "列Id"),
    GMT_CREATE("gmt_create", "创建时间"),
    GMT_MODIFIED("gmt_modified", "更新时间"),
    ;


    private final String column;
    private final String label;


    BaseEnum(String column, String label) {
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
