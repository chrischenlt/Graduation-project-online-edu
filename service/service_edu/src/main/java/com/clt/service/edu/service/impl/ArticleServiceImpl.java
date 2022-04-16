package com.clt.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clt.common.base.enums.BaseEnum;
import com.clt.common.base.result.R;
import com.clt.service.edu.entity.*;
import com.clt.service.edu.entity.form.ArticleInfoForm;
import com.clt.service.edu.entity.vo.ArticleVo;
import com.clt.service.edu.feign.OssFileService;
import com.clt.service.edu.mapper.ArticleContentMapper;
import com.clt.service.edu.mapper.ArticleMapper;
import com.clt.service.edu.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Author 陈力天
 * @Date 2022/4/1
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {


    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private OssFileService ossFileService;

    @Override
    public String saveArticleInfo(ArticleInfoForm articleInfoForm) {

        Article article = new Article();
        BeanUtils.copyProperties(articleInfoForm, article);
        baseMapper.insert(article);

        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleContent(articleInfoForm.getArticleContent());
        articleContent.setId(article.getId());
        articleContentMapper.insert(articleContent);

        return article.getId();
    }

    @Override
    public ArticleInfoForm getArticleInfoById(String id) {
        return this.baseMapper.getCourseInfoById(id);
    }

    @Override
    public void updateArticleInfoById(ArticleInfoForm articleInfoForm) {
        Article article = new Article();
        BeanUtils.copyProperties(articleInfoForm, article);
        baseMapper.updateById(article);

        ArticleContent articleContent = new ArticleContent();
        articleContent.setArticleContent(articleInfoForm.getArticleContent());
        articleContent.setId(articleInfoForm.getId());

        articleContentMapper.updateById(articleContent);
    }

    @Override
    public IPage<Article> selectPage(Long page, Long limit) {

        // 根据创建时间排序
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(BaseEnum.GMT_CREATE.getColumn());

        //组装分页
        Page<Article> pageParam = new Page<>(page, limit);

        //执行分页查询
        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public void removeCoverById(String id) {
        Article article = baseMapper.selectById(id);
        if (Objects.nonNull(article)) {
            // 异步处理
            threadPoolTaskExecutor.execute(() -> {
                String cover = article.getCover();
                if (!StringUtils.isEmpty(cover)) {
                    R r = ossFileService.removeFile(cover);
                    if (!r.getSuccess()) {
                        log.error("oss课程封面图片删除失败, cover : {}", cover);
                    }
                }
            });
        }
    }

    @Override
    public boolean removeArticleById(String id) {
        // 异步线程进行相关处理，提高页面请求响应速度
        threadPoolTaskExecutor.execute(() -> {
            //根据articleId删除CourseDescription(课程详情)
            articleContentMapper.deleteById(id);
        });

        return this.removeById(id);
    }

    @Override
    public List<Article> webSelectList() {
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.orderByDesc(BaseEnum.GMT_CREATE.getColumn());
        return this.baseMapper.selectList(articleQueryWrapper);
    }

    @Override
    public ArticleVo selectWebCourseVoById(String articleId, String userId) {
        if (StringUtils.isEmpty(articleId)) {
            return null;
        }

        Article article = baseMapper.selectById(articleId);

        ArticleContent articleContent = articleContentMapper.selectById(articleId);

        ArticleVo articleVo = new ArticleVo();

        // todo

        BeanUtils.copyProperties(article, articleVo);
        BeanUtils.copyProperties(articleContent, articleVo);

        //返回文章相关信息
        return articleVo;
    }
}
