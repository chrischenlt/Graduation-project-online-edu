package com.clt.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 陈力天
 * @Date 2022/1/30
 */
@Data
public class VideoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Boolean free;
    private Integer sort;

    private String videoSourceId;
}