package com.clt.service.edu.controller.admin;


import com.clt.common.base.result.R;
import com.clt.service.edu.entity.Chapter;
import com.clt.service.edu.entity.vo.ChapterVo;
import com.clt.service.edu.service.ChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@CrossOrigin
@Api(description = "章节管理")
@RestController
@RequestMapping("/admin/edu/chapter")
@Slf4j
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @ApiOperation("新增章节")
    @PostMapping("save")
    public R save(
            @ApiParam(value = "章节对象", required = true)
            @RequestBody Chapter chapter) {
        if (chapterService.save(chapter)) {
            return R.ok().message("保存成功");
        }
        return R.error().message("保存失败");
    }

    @ApiOperation("根据id查询章节")
    @GetMapping("get/{id}")
    public R getById(
            @ApiParam(value = "章节id", required = true)
            @PathVariable String id) {

        Chapter chapter;
        if (Objects.nonNull(chapter = chapterService.getById(id))) {
            return R.ok().data("item", chapter);
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("根据id修改章节")
    @PutMapping("update")
    public R updateById(
            @ApiParam(value = "章节对象", required = true)
            @RequestBody Chapter chapter) {

        if (chapterService.updateById(chapter)) {
            return R.ok().message("修改成功");
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("根据ID删除章节")
    @DeleteMapping("remove/{id}")
    public R removeById(
            @ApiParam(value = "章节ID", required = true)
            @PathVariable String id) {

        //TODO: 删除课程视频
        //此处调用vod中的删除视频文件的接口

        //删除章节
        if (chapterService.removeChapterById(id)) {
            return R.ok().message("删除成功");
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("嵌套章节数据列表")
    @GetMapping("nested-list/{courseId}")
    public R nestedListByCourseId(
            @ApiParam(value = "课程ID", required = true)
            @PathVariable String courseId) {

        List<ChapterVo> chapterVoList = chapterService.nestedList(courseId);
        return R.ok().data("items", chapterVoList);
    }

}

