package com.clt.service.edu.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/9
 */
public enum CommentEnum {

    COURSE_ID("course_id", "课程ID"),
    TEACHER_ID("teacher_id", "讲师ID"),
    MEMBER_ID("member_id", "会员id"),
    NICKNAME("nickname", "会员昵称"),
    AVATAR("avatar", "会员头像"),
    CONTENT("content", "评论内容"),
    ;

    private final String column;
    private final String label;

    CommentEnum(String column, String label) {
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
