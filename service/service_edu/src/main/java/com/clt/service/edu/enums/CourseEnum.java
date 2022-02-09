package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/9
 */
public enum CourseEnum {

    TEACHER_ID("teacher_id", "课程讲师ID"),
    SUBJECT_ID("subject_id","课程专业ID"),
    SUBJECT_PARENT_ID("subject_parent_id","课程专业父级ID"),
    TITLE("title", "课程标题"),
    PRICE("price","课程销售价格，设置为0则可免费观看"),
    LESSON_NUM("lesson_num","总课时"),
    COVER("cover","课程封面图片路径"),
    BUY_COUNT("buy_count","销售数量"),
    VIEW_COUNT("view_count","浏览数量"),
    VERSION("version","乐观锁"),
    STATUS("status","课程状态 Draft未发布  Normal已发布"),
    ;

    private final String column;
    private final String label;

    CourseEnum(String column, String label) {
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
