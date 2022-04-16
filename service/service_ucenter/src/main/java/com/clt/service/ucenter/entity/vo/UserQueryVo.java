package com.clt.service.ucenter.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 陈力天
 * @Date 2022/4/16
 */
@Data
public class UserQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;

    private String mobile;
}
