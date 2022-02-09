package com.clt.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.service.edu.entity.Video;
import com.clt.service.edu.enums.VideoEnum;
import com.clt.service.edu.feign.VodMediaService;
import com.clt.service.edu.mapper.VideoMapper;
import com.clt.service.edu.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Resource
    private VodMediaService vodMediaService;

    @Override
    public void removeMediaVideoById(String id) {

        //根据videoId找到视频id
        Video video = baseMapper.selectById(id);
        String videoSourceId = video.getVideoSourceId();
        vodMediaService.removeVideo(videoSourceId);
    }

    @Override
    public void removeMediaVideoByChapterId(String chapterId) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(VideoEnum.VIDEO_SOURCE_ID.getColumn());
        queryWrapper.eq(VideoEnum.CHAPTER_ID.getColumn() , chapterId);

        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        List<String> videoSourceIdList = maps.stream().map(data -> {
            return (String) data.get(VideoEnum.VIDEO_SOURCE_ID.getColumn());
        }).collect(Collectors.toList());

        vodMediaService.removeVideoByIdList(videoSourceIdList);

    }

    @Override
    public void removeMediaVideoByCourseId(String courseId) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(VideoEnum.VIDEO_SOURCE_ID.getColumn());
        queryWrapper.eq(VideoEnum.COURSE_ID.getColumn(), courseId);

        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        List<String> videoSourceIdList = maps.stream().map(data -> {
            return (String) data.get(VideoEnum.VIDEO_SOURCE_ID.getColumn());
        }).collect(Collectors.toList());

        vodMediaService.removeVideoByIdList(videoSourceIdList);

    }
}
