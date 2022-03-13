package com.clt.service.edu.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.common.base.enums.BaseEnum;
import com.clt.common.base.util.TimeUtils;
import com.clt.service.edu.entity.Course;
import com.clt.service.edu.entity.CourseCollect;
import com.clt.service.edu.entity.CourseForRedis;
import com.clt.service.edu.entity.CourseLike;
import com.clt.service.edu.entity.vo.CourseCollectVo;
import com.clt.service.edu.entity.vo.WebCourseVo;
import com.clt.service.edu.enums.CourseCollectEnum;
import com.clt.service.edu.mapper.CourseCollectMapper;
import com.clt.service.edu.mapper.CourseLikeMapper;
import com.clt.service.edu.mapper.CourseMapper;
import com.clt.service.edu.service.CourseCollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RSortedSet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private Redisson redisson;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CourseLikeMapper courseLikeMapper;

    // 课程收藏相关key
    private static final String RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY = "Record_All_Course_Collect_By_UserId:";
    private static final String ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY = "Zset_Record_All_Course_Collect_By_UserId:";
    private static final String COURSE_COLLECT_COUNT = "Course_Collect_Count_CourseId:";
    private static final String COURSE_COLLECT_LIST_BY_USERID_KEY = "Course_Collect_List_By_UserId:";

    // 课程喜欢相关key
    private static final String RECORD_ALL_COURSE_LIKE_BY_USERID_KEY = "Record_All_Course_Like_By_UserId:";
    private static final String ZSET_RECORD_ALL_COURSE_LIKE_BY_USERID_KEY = "Zset_Record_All_Course_Like_By_UserId:";
    private static final String COURSE_LIKE_COUNT = "Course_Like_Count_CourseId:";


    private static final String COLLECT = "1";
    private static final String UNCOLLECT = "2";


    private static final String LIKE = "1";
    private static final String UNLIKE = "2";

    private static final String COURSE_FOR_REDIS_COURSEID_KEY = "CourseForRedis_CourseId:";

    private HashOperations<String, String, String> opsForHash;
    private ValueOperations<String, String> opsForValue;
    private ZSetOperations<String, String> opsForZSet;
    private ListOperations<String, String> opsForList;


    @PostConstruct
    private void init() {
        opsForValue = redisTemplate.opsForValue();
        opsForZSet = redisTemplate.opsForZSet();
        opsForList = redisTemplate.opsForList();
    }



    @Override
    public List<CourseCollectVo> selectCourseCollectListByUserId(String userId) {
        // 从redis的zset中取出最新保存的10条数据
        Set<String> courseIdSet = opsForZSet.reverseRange(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, 0, 10);

        if (CollectionUtils.isEmpty(courseIdSet)) {
            QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(CourseCollectEnum.USER_ID.getColumn(), userId);
            queryWrapper.orderByDesc(BaseEnum.GMT_CREATE.getColumn());
            queryWrapper.last("limit 10");
            List<CourseCollect> courseCollectList = this.baseMapper.selectList(queryWrapper);
            courseIdSet = new HashSet<>();
            for (CourseCollect courseCollect : courseCollectList) {
                opsForZSet.add(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, courseCollect.getCourseId(), (double) courseCollect.getGmtCreate().getTime());
                courseIdSet.add(courseCollect.getCourseId());
            }
            // 设置过期时间
            redisTemplate.expire(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, 12, TimeUnit.HOURS);
        }
        List<CourseCollectVo> result = new ArrayList<>(10);
        // 通过courseId从redis获取对应数据
        courseIdSet.forEach(courseId -> {
            String courseForRedisJson = opsForValue.get(COURSE_FOR_REDIS_COURSEID_KEY + courseId);
            if (StringUtils.isEmpty(courseForRedisJson)) {
                RLock redissonLock = redisson.getLock(courseId);
                if (redissonLock.tryLock()) {
                    try {
                        WebCourseVo webCourseVo = courseMapper.selectWebCourseVoById(courseId);
                        if (Objects.isNull(webCourseVo)) {
                            return;
                        }
                        CourseForRedis courseForRedis = new CourseForRedis();
                        BeanUtils.copyProperties(webCourseVo, courseForRedis);

                        opsForValue.set(COURSE_FOR_REDIS_COURSEID_KEY + courseId, JSON.toJSONString(courseForRedis), 24, TimeUnit.HOURS);

                        CourseCollectVo courseCollectVo = new CourseCollectVo();
                        BeanUtils.copyProperties(courseForRedis, courseCollectVo);

                        addCollectTime(courseCollectVo, userId, courseId);

                        result.add(courseCollectVo);

                    } finally {
                        if (redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()) {
                            redissonLock.unlock();
                        }
                    }
                } else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(600);
                        courseForRedisJson = opsForValue.get(COURSE_FOR_REDIS_COURSEID_KEY + courseId);
                        if (!StringUtils.isEmpty(courseForRedisJson)) {
                            CourseForRedis courseForRedis = JSON.parseObject(courseForRedisJson, CourseForRedis.class);
                            CourseCollectVo courseCollectVo = new CourseCollectVo();
                            BeanUtils.copyProperties(courseForRedis, courseCollectVo);

                            // 加上收藏时间
                            addCollectTime(courseCollectVo, userId, courseId);

                            result.add(courseCollectVo);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                CourseForRedis courseForRedis = JSON.parseObject(courseForRedisJson, CourseForRedis.class);
                CourseCollectVo courseCollectVo = new CourseCollectVo();
                BeanUtils.copyProperties(courseForRedis, courseCollectVo);

                addCollectTime(courseCollectVo, userId, courseId);

                result.add(courseCollectVo);
            }
        });
        return result;
    }

    private void addCollectTime(CourseCollectVo courseCollectVo, String userId, String courseId) {
        Double score = opsForZSet.score(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, courseId);
        assert score != null;
        BigDecimal bd = new BigDecimal(score.toString());
        String s = bd.toPlainString();
        String formatDate = TimeUtils.getFormatDate(Long.parseLong(s));
        courseCollectVo.setGmtCreate(formatDate);
    }


    /**
     * 判断该课程用户是否收藏
     */
    @Override
    public boolean isCollect(String courseId, String userId) {
        QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CourseCollectEnum.COURSE_ID.getColumn(), courseId);
        queryWrapper.eq(CourseCollectEnum.USER_ID.getColumn(), userId);
        List<CourseCollect> courseCollects = this.baseMapper.selectList(queryWrapper);
        return courseCollects.size() > 0;
    }


    /**
     * 更新redis中课程喜欢相关数据
     */
    @Override
    public void insertOrUpdateCourseLike(String courseId, String isLike, String userId) {
        // 记录点赞数,将用户是否点赞得相关信息用hash结构保存在redis中，点赞：有值 ，未点赞：null；
        if (LIKE.equals(isLike)) {
            opsForValue.increment(COURSE_LIKE_COUNT + courseId);
            CourseLike courseLike = new CourseLike();
            courseLike.setCourseId(courseId);
            courseLike.setUserId(userId);
            courseLikeMapper.insert(courseLike);
        } else if (UNLIKE.equals(isLike)) {
            opsForValue.decrement(COURSE_LIKE_COUNT + courseId);
            QueryWrapper<CourseLike> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(CourseCollectEnum.COURSE_ID.getColumn(), courseId);
            queryWrapper.eq(CourseCollectEnum.USER_ID.getColumn(), userId);
            courseLikeMapper.delete(queryWrapper);
        }
    }

    @Override
    public void insertOrUpdateCourseCollect(String courseId, String isCollect, String userId) {
        // 记录点赞数，将用户是否点赞得相关信息用zset结构保存在redis中，点赞：有值 ，未点赞：null；
        if (COLLECT.equals(isCollect)) {
            opsForValue.increment(COURSE_COLLECT_COUNT + courseId);
            CourseCollect courseCollect = new CourseCollect();
            courseCollect.setCourseId(courseId);
            courseCollect.setUserId(userId);
            this.baseMapper.insert(courseCollect);
        } else if (UNCOLLECT.equals(isCollect)) {
            opsForValue.decrement(COURSE_COLLECT_COUNT + courseId);
            QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(CourseCollectEnum.COURSE_ID.getColumn(), courseId);
            queryWrapper.eq(CourseCollectEnum.USER_ID.getColumn(), userId);
            this.baseMapper.delete(queryWrapper);
        }
        // 删除redis中的收藏列表
        redisTemplate.delete(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId);
    }

    @Override
    public String getCourseLikeCount(String courseId) {

        String count = opsForValue.get(COURSE_LIKE_COUNT + courseId);

        if (StringUtils.isEmpty(count)) {
            return "0";
        }
        return count;
    }

    @Override
    public String getCourseCollectCount(String courseId) {

        String count = opsForValue.get(COURSE_COLLECT_COUNT + courseId);

        if (StringUtils.isEmpty(count)) {
            return "0";
        }
        return count;
    }


    /**
     * 判断该课程用户是否喜欢
     */
    @Override
    public boolean isLike(String courseId, String userId) {
        QueryWrapper<CourseLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CourseCollectEnum.COURSE_ID.getColumn(), courseId);
        queryWrapper.eq(CourseCollectEnum.USER_ID.getColumn(), userId);
        List<CourseLike> courseCollects = courseLikeMapper.selectList(queryWrapper);
        return courseCollects.size() > 0;
    }


}
