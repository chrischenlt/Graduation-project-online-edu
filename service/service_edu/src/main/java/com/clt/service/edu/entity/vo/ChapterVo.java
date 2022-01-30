package com.clt.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/1/30
 */
@Data
public class ChapterVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private Integer sort;
    private List<VideoVo> children = new ArrayList<>();
}