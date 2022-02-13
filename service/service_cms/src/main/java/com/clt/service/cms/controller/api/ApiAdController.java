package com.clt.service.cms.controller.api;

import com.clt.common.base.result.R;
import com.clt.service.cms.entity.Ad;
import com.clt.service.cms.service.AdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/2/4
 */

@Api(description = "广告推荐")
@RestController
@RequestMapping("/api/cms/ad")
@Slf4j
public class ApiAdController {

    @Autowired
    private AdService adService;

    @ApiOperation("根据推荐位id希纳是广告推荐")
    @GetMapping("list/{adTypeId}")
    public R listByAdTypeId(@ApiParam(value = "推荐位id", required = true) @PathVariable String adTypeId) {

        List<Ad> adList = adService.selectByAdTypeId(adTypeId);
        return R.ok().data("items", adList);
    }
}
