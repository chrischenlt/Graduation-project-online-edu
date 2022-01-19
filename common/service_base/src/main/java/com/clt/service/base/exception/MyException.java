package com.clt.service.base.exception;

import com.clt.common.base.result.ResultCodeEnum;
import lombok.Data;

/**
 * @Author 陈力天
 * @Date 2022/1/16
 */
@Data
public class MyException extends RuntimeException {


    private Integer code;

    public MyException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public MyException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "MyException{" +
                "code=" + code +
                ", message=" + this.getMessage()+
                '}';
    }
}
