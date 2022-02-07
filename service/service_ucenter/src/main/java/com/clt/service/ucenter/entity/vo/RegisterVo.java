package com.clt.service.ucenter.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 陈力天
 * @Date 2022/2/6
 */
@Data
public class RegisterVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nickname;
    private String mobile;
    private String password;
    private String code;
}