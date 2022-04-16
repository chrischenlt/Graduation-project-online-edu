package com.clt.service.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clt.service.edu.entity.Article;
import com.clt.service.edu.entity.form.ArticleInfoForm;
import com.clt.service.edu.entity.vo.ArticleVo;

import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/4/1
 */
public interface ArticleService extends IService<Article> {
    String saveArticleInfo(ArticleInfoForm articleInfoForm);

    ArticleInfoForm getArticleInfoById(String id);

    void updateArticleInfoById(ArticleInfoForm articleInfoForm);

    IPage<Article> selectPage(Long page, Long limit);

    void removeCoverById(String id);

    boolean removeArticleById(String id);

    List<Article> webSelectList();

    ArticleVo selectWebCourseVoById(String articleId, String userId);
}
