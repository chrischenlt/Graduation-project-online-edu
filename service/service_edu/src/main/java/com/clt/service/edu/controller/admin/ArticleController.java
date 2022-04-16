package com.clt.service.edu.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.clt.common.base.result.R;
import com.clt.service.edu.entity.Article;
import com.clt.service.edu.entity.form.ArticleInfoForm;
import com.clt.service.edu.entity.form.CourseInfoForm;
import com.clt.service.edu.entity.vo.CourseQueryVo;
import com.clt.service.edu.entity.vo.CourseVo;
import com.clt.service.edu.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @Author 陈力天
 * @Date 2022/4/1
 */
@Api(description = "文章管理")
@RestController
@RequestMapping("/admin/edu/article")
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("新增文章")
    @PostMapping("save-article-info")
    public R saveCourseInfo(
            @ApiParam(value = "文章基本信息", required = true)
            @RequestBody ArticleInfoForm articleInfoForm) {
        String articleId = articleService.saveArticleInfo(articleInfoForm);
        return R.ok().data("articleId", articleId).message("保存成功");
    }


    @ApiOperation("根据ID查询文章")
    @GetMapping("article-info/{id}")
    public R getCourseById(
            @ApiParam(value = "文章ID", required = true)
            @PathVariable String id) {

        if (StringUtils.isEmpty(id)) {
            return R.ok().message("数据不存在");
        }

        ArticleInfoForm articleInfoForm = articleService.getArticleInfoById(id);

        if (Objects.nonNull(articleInfoForm)) {
            return R.ok().data("item", articleInfoForm);
        }

        return R.ok().message("数据不存在");
    }


    @ApiOperation("更新文章")
    @PutMapping("update-article-info")
    public R updateCourseById(@ApiParam(value = "课程Id", required = true)
                              @RequestBody ArticleInfoForm articleInfoForm) {

        articleService.updateArticleInfoById(articleInfoForm);

        return R.ok().message("数据更新成功");
    }

    @ApiOperation("文章分页列表")
    @GetMapping("list/{page}/{limit}")
    public R listPage(@ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit){

        IPage<Article> pageModel = articleService.selectPage(page, limit);
        List<Article> records = pageModel.getRecords();
        long total = pageModel.getTotal();
        return R.ok().data("total", total).data("rows", records);
    }

    @ApiOperation("根据Id删除文章")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam(value = "文章Id" ,required = true) @PathVariable String id) {
        //删除课程封面
        articleService.removeCoverById(id);

        //删除课程
        if (articleService.removeArticleById(id)) {
            return R.ok().message("删除成功");
        }
        return R.ok().message("删除失败");
    }
}
