package com.clt.service.edu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clt.service.base.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author 陈力天
 * @Date 2022/4/1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("edu_article_content")
public class ArticleContent extends BaseEntity {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.NONE)
    private String id;

    private String articleContent;
}
