package com.clt.service.edu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.clt.service.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author 陈力天
 * @Date 2022/3/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("edu_course_like")
@ApiModel(value="CourseLike对象", description="课程喜欢")
public class CourseLike extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "课程ID")
    private String courseId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

}
