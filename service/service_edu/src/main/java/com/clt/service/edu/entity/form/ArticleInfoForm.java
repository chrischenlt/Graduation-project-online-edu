package com.clt.service.edu.entity.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 陈力天
 * @Date 2022/4/15
 */
@Data
public class ArticleInfoForm implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private String introduce;

    private String title;

    private String cover;

    private String articleContent;

}
