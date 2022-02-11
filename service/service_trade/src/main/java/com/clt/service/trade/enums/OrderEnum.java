package com.clt.service.trade.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/11
 */
public enum OrderEnum {

    ORDER_NO("order_no", "订单号"),
    COURSE_ID("course_id", "课程id"),
    COURSE_TITLE("course_title", "课程名称"),
    COURSE_COVER("course_cover", "课程封面"),
    TEACHER_NAME("teacher_name", "讲师名称"),
    MEMBER_ID("member_id", "会员id"),
    NICKNAME("nickname", "会员昵称"),
    MOBILE("mobile", "会员手机"),
    TOTAL_FEE("total_fee", "订单金额（分）"),
    PAY_TYPE("pay_type", "支付类型（1：微信 2：支付宝）"),
    STATUS("status", "订单状态（0：未支付 1：已支付）"),
    IS_DELETED("is_deleted", "逻辑删除 1（true）已删除， 0（false）未删除"),
    ;


    private final String column;
    private final String label;

    OrderEnum(String column, String label) {
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
