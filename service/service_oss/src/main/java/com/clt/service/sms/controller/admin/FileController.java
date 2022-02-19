package com.clt.service.sms.controller.admin;

import com.clt.common.base.result.R;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.ExceptionUtils;
import com.clt.service.base.exception.MyException;
import com.clt.service.sms.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @Author 陈力天
 * @Date 2022/1/16
 */
@Api(description = "阿里云文件管理")

@RestController
@RequestMapping("/admin/oss/file")
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("文件上传")
    @PostMapping("upload")
    public R upload(@ApiParam(value = "文件", required = true)
                    @RequestParam("file") MultipartFile file,
                    @ApiParam(value = "模块", required = true)
                    @RequestParam("module") String module) {

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String uploadUrl = fileService.upload(inputStream, module, originalFilename);
            log.info("文件上传成功 file: {}, module: {}", file, module);
            return R.ok().message("文件上传成功").data("url", uploadUrl);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new MyException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }

    }


    @ApiOperation(value = "文件删除")
    @DeleteMapping("remove")
    public R removeFile(@ApiParam(value = "要删除的文件url路径", required = true) @RequestBody String url) {
        fileService.removeFile(url);
        return R.ok().message("文件删除成功");
    }






}
