package com.clt.service.edu.controller.api;

import com.clt.common.base.result.R;
import com.clt.common.base.util.JwtInfo;
import com.clt.common.base.util.JwtUtils;
import com.clt.service.edu.entity.Article;
import com.clt.service.edu.entity.Course;
import com.clt.service.edu.entity.vo.ArticleVo;
import com.clt.service.edu.entity.vo.ChapterVo;
import com.clt.service.edu.entity.vo.WebCourseQueryVo;
import com.clt.service.edu.entity.vo.WebCourseVo;
import com.clt.service.edu.mapper.ArticleMapper;
import com.clt.service.edu.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @Author 陈力天
 * @Date 2022/4/16
 */
@Api(description="文章")
@RestController
@RequestMapping("/api/edu/article")
@Slf4j
public class ApiArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("文章列表")
    @GetMapping("list")
    public R pageList(){

        List<Article> courseList = articleService.webSelectList();

        return R.ok().data("articleList", courseList);
    }

    @ApiOperation("根据id查询文章")
    @GetMapping("get/{articleId}")
    public R getById(
            @ApiParam(value = "文章id", required = true)
            @PathVariable String articleId,
            HttpServletRequest request){

        JwtInfo jwtInfo = JwtUtils.getUserIdByJwtToken(request);

        //查询文章和文章内容
        ArticleVo articleVo = articleService.selectWebCourseVoById(articleId, Objects.isNull(jwtInfo) ? null : jwtInfo.getId());

        return R.ok().data("article", articleVo);
    }
}
