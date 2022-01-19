package com.clt.service.oss.service;

import java.io.InputStream;

/**
 * @Author 陈力天
 * @Date 2022/1/16
 */
public interface FileService {

    /**
     * 阿里云oss文件上传
     */
    String upload(InputStream inputStream, String module, String originalFilename);

    /**
     * 阿里云oss 文件删除
     */
    void removeFile(String url);
}
