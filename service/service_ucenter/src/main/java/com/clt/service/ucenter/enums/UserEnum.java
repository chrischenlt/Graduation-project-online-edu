package com.clt.service.ucenter.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
public enum UserEnum {


    OPENID("openid", "微信openid"),
    MOBILE("mobile", "手机号"),
    PASSWORD("password", "密码"),
    NICKNAME("nickname", "昵称"),
    SEX("sex", "性别 1 男，2 女"),
    AGE("age", "年龄"),
    AVATAR("avatar", "用户头像"),
    SIGN("sign", "用户签名"),
    IS_DISABLED("is_disabled", "是否禁用 1（true）已禁用，  0（false）未禁用"),
    IS_DELETED("is_deleted", "逻辑删除 1（true）已删除， 0（false）未删除"),
    ;


    private final String column;
    private final String label;


    UserEnum(String column, String label) {
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
