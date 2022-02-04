package com.clt.service.vod.service;

import com.aliyuncs.exceptions.ClientException;

import java.io.InputStream;
import java.util.List;

/**
 * @Author 陈力天
 * @Date 2022/2/2
 */
public interface VideoService {

    String uploadVideo(InputStream inputStream, String originalFilename);

    void removeVideo(String videoId) throws ClientException;

    void removeVideoByIdList(List<String> videoIdList) throws ClientException;

    String getPlayAuth(String videoSourceId) throws ClientException;
}
