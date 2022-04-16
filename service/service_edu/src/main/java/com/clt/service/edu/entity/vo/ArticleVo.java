package com.clt.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 陈力天
 * @Date 2022/4/16
 */
@Data
public class ArticleVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String introduce;

    private String title;

    private String cover;

    private String articleContent;

    private Long commentCount;

    private Long likeCount;

    private Long viewCount;

    private Date gmtCreate;

}
