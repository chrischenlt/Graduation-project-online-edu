package com.clt.service.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clt.service.edu.entity.Article;
import com.clt.service.edu.entity.Chapter;
import com.clt.service.edu.entity.form.ArticleInfoForm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/4/1
 */
@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT * FROM `edu_article` ea, `edu_article_content` eac " +
            "where ea.id = #{id} and eac.id = #{id}")
    ArticleInfoForm getCourseInfoById(@Param("id") String id);

}
