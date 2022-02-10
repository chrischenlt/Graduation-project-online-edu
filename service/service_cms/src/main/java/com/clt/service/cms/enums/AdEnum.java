package com.clt.service.cms.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
public enum AdEnum {

    TITLE("title", "标题"),
    TYPE_ID("type_id", "类型ID"),
    IMAGE_URL("image_url", "图片地址"),
    COLOR("color", "背景颜色"),
    LINK_URL("link_url", "链接地址"),
    SORT("sort", "排序"),
    ;


    private final String column;
    private final String label;

    AdEnum(String column, String label) {
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
