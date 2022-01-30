package com.clt.service.edu.service;

import com.clt.service.edu.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clt.service.edu.entity.vo.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
public interface ChapterService extends IService<Chapter> {

    boolean removeChapterById(String id);

    List<ChapterVo> nestedList(String courseId);
}
