package com.clt.service.edu.controller.admin;


import com.clt.common.base.result.R;
import com.clt.service.edu.entity.Video;
import com.clt.service.edu.feign.VodMediaService;
import com.clt.service.edu.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */

@Api(description = "课时管理")
@RestController
@RequestMapping("/admin/edu/video")
@Slf4j
public class VideoController {

    @Autowired
    private VideoService videoService;

    @ApiOperation("新增课时")
    @PostMapping("save")
    public R save(
            @ApiParam(value = "课时对象", required = true)
            @RequestBody Video video) {
        if (videoService.save(video)) {
            return R.ok().message("保存成功");
        }
        return R.error().message("保存失败");
    }

    @ApiOperation("根据id查询课时")
    @GetMapping("get/{id}")
    public R getById(
            @ApiParam(value = "课时id", required = true)
            @PathVariable String id) {

        Video video;
        if (Objects.nonNull(video = videoService.getById(id))) {
            return R.ok().data("item", video);
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("根据id修改课时")
    @PutMapping("update")
    public R updateById(
            @ApiParam(value = "课时对象", required = true)
            @RequestBody Video video) {

        if (videoService.updateById(video)) {
            return R.ok().message("修改成功");
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("根据ID删除课时")
    @DeleteMapping("remove/{id}")
    public R removeById(
            @ApiParam(value = "课时ID", required = true)
            @PathVariable String id) {

        // 删除视频
        videoService.removeMediaVideoById(id);

        if (videoService.removeById(id)) {
            return R.ok().message("删除成功");
        }
        return R.error().message("数据不存在");
    }
}

