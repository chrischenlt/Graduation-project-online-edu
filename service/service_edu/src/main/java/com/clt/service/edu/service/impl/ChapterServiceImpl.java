package com.clt.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.common.base.enums.BaseEnum;
import com.clt.service.edu.entity.Chapter;
import com.clt.service.edu.entity.Video;
import com.clt.service.edu.entity.vo.ChapterVo;
import com.clt.service.edu.entity.vo.VideoVo;
import com.clt.service.edu.enums.ChapterEnum;
import com.clt.service.edu.enums.VideoEnum;
import com.clt.service.edu.mapper.ChapterMapper;
import com.clt.service.edu.mapper.VideoMapper;
import com.clt.service.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeChapterById(String id) {

        //根据courseId删除Video(课时)
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq(VideoEnum.CHAPTER_ID.getColumn(), id);
        videoMapper.delete(videoQueryWrapper);

        return this.removeById(id);
    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {

        //获取章节信息列表
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq(ChapterEnum.COURSE_ID.getColumn(), courseId);
        chapterQueryWrapper.orderByAsc(ChapterEnum.SORT.getColumn(), BaseEnum.ID.getColumn());
        List<Chapter> chapterList = baseMapper.selectList(chapterQueryWrapper);

        //获取课时信息列表
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq(VideoEnum.COURSE_ID.getColumn(), courseId);
        videoQueryWrapper.orderByAsc(VideoEnum.SORT.getColumn(), BaseEnum.ID.getColumn());
        List<Video> videoList = videoMapper.selectList(videoQueryWrapper);

        List<ChapterVo> chapterVoList = new ArrayList<>();

        chapterList.forEach(chapter -> {
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);
            chapterVoList.add(chapterVo);

            List<VideoVo> videoVoList = new ArrayList<>();
            videoList.forEach(video -> {
                if (Objects.equals(chapter.getId(), video.getChapterId())) {
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(video, videoVo);
                    videoVoList.add(videoVo);
                }
            });
            chapterVo.setChildren(videoVoList);
        });

        return chapterVoList;
    }
}
