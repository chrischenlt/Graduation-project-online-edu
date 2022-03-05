package com.clt.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.service.edu.entity.CourseCollect;
import com.clt.service.edu.entity.vo.CourseCollectVo;
import com.clt.service.edu.enums.CourseCollectEnum;
import com.clt.service.edu.mapper.CourseCollectMapper;
import com.clt.service.edu.service.CourseCollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程收藏 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@Service
public class CourseCollectServiceImpl extends ServiceImpl<CourseCollectMapper, CourseCollect> implements CourseCollectService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 课程收藏相关key
    private static final String RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY = "Record_All_Course_Collect_By_UserId:";
    private static final String COURSE_COLLECT_COUNT = "Course_Collect_Count_CourseId:";
    // 课程喜欢相关key
    private static final String RECORD_ALL_COURSE_LIKE_BY_USERID_KEY = "Record_All_Course_Like_By_UserId:";
    private static final String COURSE_LIKE_COUNT = "Course_Like_Count_CourseId:";

    private static final String COLLECT = "1";
    private static final String UNCOLLECT = "2";


    private static final String LIKE = "1";
    private static final String UNLIKE = "2";




    @PostConstruct
    private void init() {

    }

    /**
     * 判断用户是否收藏
     */
    @Override
    public boolean isCollect(String courseId, String userId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        String isLike = opsForHash.get(RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, courseId);
        if (StringUtils.isEmpty(isLike) || UNCOLLECT.equals(isLike)) {
            return false;
        }
        return true;
    }


    @Override
    public List<CourseCollectVo> selectCourseCollectListByUserId(String userId) {

        return baseMapper.selectPageByMemberId(userId);
    }


    /**
     * 更新redis中课程喜欢相关数据
     */
    @Override
    public void insertOrUpdateCourseLike(String courseId, String isLike, String userId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        // 将用户是否点赞得相关信息用hash结构保存在redis中，点赞：like = 1 ，未点赞：like = 2 或 null；
        opsForHash.put(RECORD_ALL_COURSE_LIKE_BY_USERID_KEY + userId, courseId, isLike);

        // 记录点赞数
        if (LIKE.equals(isLike)) {
            opsForValue.increment(COURSE_LIKE_COUNT + courseId);
        } else if (UNLIKE.equals(isLike)) {
            opsForValue.decrement(COURSE_LIKE_COUNT + courseId);
        }
    }

    @Override
    public void insertOrUpdateCourseCollect(String courseId, String isCollect, String userId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        // 将用户是否点赞得相关信息用hash结构保存在redis中，点赞：isCourse = 1 ，未点赞：isCourse = 2 或 null；
        opsForHash.put(RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, courseId, isCollect);

        // 记录点赞数
        if (COLLECT.equals(isCollect)) {
            opsForValue.increment(COURSE_COLLECT_COUNT + courseId);
        } else if (UNCOLLECT.equals(isCollect)) {
            opsForValue.decrement(COURSE_COLLECT_COUNT + courseId);
        }
    }

    @Override
    public String getCourseLikeCount(String courseId) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

        String count = opsForValue.get(COURSE_LIKE_COUNT + courseId);

        if (StringUtils.isEmpty(count)) {
            return "0";
        }
        return count;
    }

    @Override
    public String getCourseCollectCount(String courseId) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

        String count = opsForValue.get(COURSE_COLLECT_COUNT + courseId);

        if (StringUtils.isEmpty(count)) {
            return "0";
        }
        return count;
    }

    @Override
    public boolean isLike(String courseId, String userId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();

        String isLike = opsForHash.get(RECORD_ALL_COURSE_LIKE_BY_USERID_KEY + userId, courseId);
        if (StringUtils.isEmpty(isLike) || UNLIKE.equals(isLike)) {
            return false;
        }
        return true;
    }
}
