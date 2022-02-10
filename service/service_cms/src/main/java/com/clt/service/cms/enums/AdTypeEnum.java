package com.clt.service.cms.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
public enum AdTypeEnum {

    TITLE("title", "标题"),
    ;


    private final String column;
    private final String label;

    AdTypeEnum(String column, String label) {
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
