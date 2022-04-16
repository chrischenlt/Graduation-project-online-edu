package com.clt.service.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.clt.service.base.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author 陈力天
 * @Date 2022/4/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("edu_article")
public class Article extends BaseEntity {

    private static final long serialVersionUID=1L;

    private String title;

    private Long commentCount;

    private Long likeCount;

    private Long viewCount;

    private String cover;

    private String introduce;


}
