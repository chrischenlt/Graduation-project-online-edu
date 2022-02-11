package com.clt.service.trade.enums;

/**
 * @Author 陈力天
 * @Date 2022/2/11
 */
public enum PayLogEnum {

    ORDER_NO("order_no", "订单号"),
    PAY_TIME("pay_time", "支付完成时间"),
    TOTAL_FEE("total_fee", "支付金额（分）"),
    TRANSACTION_ID("transaction_id", "交易流水号"),
    TRADE_STATE("trade_state", "交易状态"),
    PAY_TYPE("pay_type", "支付类型（1：微信 2：支付宝）"),
    ATTR("attr", "其他属性"),
    IS_DELETED("is_deleted", "逻辑删除 1（true）已删除， 0（false）未删除"),
    ;


    private final String column;
    private final String label;

    PayLogEnum(String column, String label) {
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
