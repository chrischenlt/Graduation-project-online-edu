package com.clt.service.edu.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.common.base.result.R;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.entity.vo.TeacherQueryVo;
import com.clt.service.edu.feign.OssFileService;
import com.clt.service.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@CrossOrigin //允许跨域
@Api("讲师管理")
@RestController
@RequestMapping("/admin/edu/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;


    @ApiOperation("所有讲师列表")
    @GetMapping("list")
    public R listAll() {
        List<Teacher> teacherList = teacherService.list();
        return R.ok().data("item", teacherList);
    }


    @ApiOperation("根据Id删除讲师")
    @DeleteMapping("remove/{id}")
    public R removeById(@ApiParam("讲师Id") @PathVariable String id) {

        teacherService.removeAvatarById(id);

        if (teacherService.removeById(id)) {
            return R.ok().message("删除成功");
        }
        return R.ok().message("删除失败");
    }

    @ApiOperation("讲师分页列表")
    @GetMapping("list/{page}/{limit}")
    public R listPage(@ApiParam("当前页码") @PathVariable Long page,
                      @ApiParam("每页记录数") @PathVariable Long limit,
                      TeacherQueryVo teacherQueryVo) {

        Page<Teacher> pageParam = new Page<>(page, limit);
        IPage<Teacher> pageModel = teacherService.selectPage(pageParam, teacherQueryVo);
        List<Teacher> records = pageModel.getRecords();
        long total = pageModel.getTotal();
        return R.ok().data("total", total).data("rows", records);
    }

    @ApiOperation("新增讲师")
    @PostMapping("save")
    public R save(@ApiParam("讲师对象") @RequestBody Teacher teacher) {
        boolean saveSuccess = teacherService.save(teacher);
        if (saveSuccess) {
            return R.ok().message("保存成功");
        }
        return R.error().message("保存失败");
    }

    @ApiOperation("更新讲师资料")
    @PostMapping("update")
    public R updateById(@ApiParam("讲师对象") @RequestBody Teacher teacher) {
        boolean updateSuccess = teacherService.updateById(teacher);
        if (updateSuccess) {
            return R.ok().message("更新成功");
        }
        return R.error().message("更新失败");
    }


    @ApiOperation("根据id获取讲师信息")
    @GetMapping("get/{id}")
    public R getDataById(@ApiParam("讲师对象") @PathVariable String id) {
        Teacher teacher = teacherService.getById(id);
        if (Objects.nonNull(teacher)) {
            return R.ok().data("item", teacher);
        }
        return R.error().message("数据不存在");
    }


    @ApiOperation("根据Id列表删除讲师")
    @DeleteMapping("batch-remove")
    public R removeRows(@ApiParam(value = "讲师Id", required = true) @RequestBody List<String> idList) {
        if (teacherService.removeByIds(idList)) {
            return R.ok().message("删除成功");
        }
        return R.ok().message("删除失败");
    }


    @ApiOperation("根据关键词查询讲师名列表")
    @GetMapping("list/name/{key}")
    public R selectNameListByKey(
            @ApiParam(value = "关键词", required = true)
            @PathVariable String key) {
        List<Map<String, Object>> nameList = teacherService.selectNameList(key);
        return R.ok().data("nameList", nameList);
    }

}

