package com.clt.service.edu.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.common.base.enums.BaseEnum;
import com.clt.common.base.util.TimeUtils;
import com.clt.service.edu.entity.*;
import com.clt.service.edu.entity.vo.CourseCollectVo;
import com.clt.service.edu.entity.vo.WebCourseVo;
import com.clt.service.edu.enums.CourseCollectEnum;
import com.clt.service.edu.enums.CourseEnum;
import com.clt.service.edu.mapper.CourseCollectMapper;
import com.clt.service.edu.mapper.CourseLikeMapper;
import com.clt.service.edu.mapper.CourseMapper;
import com.clt.service.edu.service.CourseCollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RSortedSet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import springfox.documentation.spring.web.json.Json;

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
@Slf4j
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

    // 课程购买总数key
    private static final String COURSE_BUY_COUNT = "Course_Buy_Count_CourseId:";


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
        // 从redis的zset中取出最新保存的10条数据, set中的内容是CourseCollectForRedis转换成json串的内容
        Set<String> courseCollectForRedisSet = opsForZSet.reverseRange(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, 0, 10);

        if (CollectionUtils.isEmpty(courseCollectForRedisSet)) {
            QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(CourseCollectEnum.USER_ID.getColumn(), userId);
            queryWrapper.orderByDesc(BaseEnum.GMT_CREATE.getColumn());
            queryWrapper.last("limit 10");
            List<CourseCollect> courseCollectList = this.baseMapper.selectList(queryWrapper);

            // 如果MySQL查询出来的数据为空难则表示该用户没有收藏过课程
            if (CollectionUtils.isEmpty(courseCollectList)) {
                return new ArrayList<>(0);
            }

            List<CourseCollectForRedis> courseCollectForRedisList = new ArrayList<>(courseCollectList.size());

            courseCollectList.forEach(courseCollect -> {
                CourseCollectForRedis courseCollectForRedis = new CourseCollectForRedis();
                BeanUtils.copyProperties(courseCollect, courseCollectForRedis);
                courseCollectForRedisList.add(courseCollectForRedis);
            });

            courseCollectForRedisSet = new HashSet<>(16);
            for (CourseCollectForRedis courseCollectForRedis : courseCollectForRedisList) {
                String jsonString = JSON.toJSONString(courseCollectForRedis);
                opsForZSet.add(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, jsonString, (double) courseCollectForRedis.getGmtCreate().getTime());
                courseCollectForRedisSet.add(jsonString);
            }
            // 设置过期时间
            redisTemplate.expire(ZSET_RECORD_ALL_COURSE_COLLECT_BY_USERID_KEY + userId, 12, TimeUnit.HOURS);
        }

        // 课程id信息，方便后面到redis中multiGet课程详细信息。
        List<String> courseIdKeyList = new ArrayList<>(10);

        Map<String, CourseCollectVo> courseId2CourseCollectVo = courseCollectForRedisSet.stream()
                .map(jsonString -> JSON.parseObject(jsonString, CourseCollectForRedis.class))
                .collect(Collectors.toMap(data -> {
                    courseIdKeyList.add(COURSE_FOR_REDIS_COURSEID_KEY + data.getCourseId());
                    return data.getCourseId();
                }, data -> {
                    String courseId = data.getCourseId();
                    Date gmtCreate = data.getGmtCreate();

                    // 转化日期格式
                    BigDecimal bd = new BigDecimal(Long.toString(gmtCreate.getTime()));
                    String s = bd.toPlainString();
                    String formatDate = TimeUtils.getFormatDate(Long.parseLong(s));

                    CourseCollectVo courseCollectVo = new CourseCollectVo();
                    courseCollectVo.setGmtCreate(formatDate);
                    courseCollectVo.setId(courseId);
                    return courseCollectVo;
                }));

        // 课程信息json串
        List<String> courseForRedisJsonString = opsForValue.multiGet(courseIdKeyList);
        assert !CollectionUtils.isEmpty(courseForRedisJsonString);
        List<String> courseForRedisJsonStringNotNull = courseForRedisJsonString.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(courseForRedisJsonStringNotNull)) {
            courseForRedisJsonStringNotNull.forEach(courseForRedisJson -> {
                CourseForRedis courseForRedis = JSON.parseObject(courseForRedisJson, CourseForRedis.class);

                String courseId = courseForRedis.getId();
                CourseCollectVo courseCollectVo = courseId2CourseCollectVo.get(courseId);
                BeanUtils.copyProperties(courseForRedis, courseCollectVo);
                courseId2CourseCollectVo.put(courseId, courseCollectVo);

                courseIdKeyList.remove(COURSE_FOR_REDIS_COURSEID_KEY + courseId);
            });
        }

        // 如果courseIdKeyList不为空，说明部分课程信息缓存不存在，需要重新缓存一次
        if (!CollectionUtils.isEmpty(courseIdKeyList)) {
            courseIdKeyList.forEach(courseIdKey -> {
                String courseId = courseIdKey.split(":")[1];
                // 根据课程id获取分布式锁，避免高并发情况下缓存击穿的情况
                RLock redissonLock = redisson.getLock(courseId);
                if (redissonLock.tryLock()) {
                    try {
                        WebCourseVo webCourseVo = courseMapper.selectWebCourseVoById(courseId);
                        if (Objects.isNull(webCourseVo)) {
                            log.error("can not find webCouseVo, courseId = {}", courseId);
                            return;
                        }
                        CourseCollectVo courseCollectVo = courseId2CourseCollectVo.get(courseId);
                        CourseForRedis courseForRedis = new CourseForRedis();
                        BeanUtils.copyProperties(webCourseVo, courseForRedis);

                        // 将课程信息缓存到redis中
                        opsForValue.set(COURSE_FOR_REDIS_COURSEID_KEY + courseId, JSON.toJSONString(courseForRedis), 24, TimeUnit.HOURS);

                        BeanUtils.copyProperties(courseForRedis, courseCollectVo);
                        courseId2CourseCollectVo.put(courseId, courseCollectVo);

                    } finally {
                        if (redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()) {
                            redissonLock.unlock();
                        }
                    }
                } else {
                    try {
                        // 可能其他线程正在执行缓存课程信息操作
                        TimeUnit.MILLISECONDS.sleep(600);
                        String courseForRedisJson = opsForValue.get(COURSE_FOR_REDIS_COURSEID_KEY + courseId);
                        if (!StringUtils.isEmpty(courseForRedisJson)) {
                            CourseForRedis courseForRedis = JSON.parseObject(courseForRedisJson, CourseForRedis.class);
                            CourseCollectVo courseCollectVo = courseId2CourseCollectVo.get(courseId);

                            BeanUtils.copyProperties(courseForRedis, courseCollectVo);

                            courseId2CourseCollectVo.put(courseId, courseCollectVo);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return new ArrayList<>(courseId2CourseCollectVo.values());
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
            Long likeCount = opsForValue.increment(COURSE_LIKE_COUNT + courseId);
            // 每10个收藏持久化到mysql
            if (likeCount % 10 == 0) {
                Course course = new Course();
                course.setLikeCount(likeCount);
                courseMapper.updateById(course);
            }
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

    @Override
    public String getCourseBuyCount(String courseId) {

        String buyCount = opsForValue.get(COURSE_BUY_COUNT + courseId);
        if (StringUtils.isEmpty(buyCount)) {
            Course course = courseMapper.selectById(courseId);
            Long buyCountFromDB = course.getBuyCount();
            buyCount = String.valueOf(buyCountFromDB);
            opsForValue.set(COURSE_BUY_COUNT + courseId, String.valueOf(buyCountFromDB));
        }

        return buyCount;
    }


}
