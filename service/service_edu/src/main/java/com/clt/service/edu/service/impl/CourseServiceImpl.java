package com.clt.service.edu.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.common.base.enums.BaseEnum;
import com.clt.common.base.result.R;
import com.clt.service.base.dto.CourseDto;
import com.clt.service.edu.entity.*;
import com.clt.service.edu.entity.form.CourseInfoForm;
import com.clt.service.edu.entity.vo.*;
import com.clt.service.edu.enums.*;
import com.clt.service.edu.feign.OssFileService;
import com.clt.service.edu.mapper.*;
import com.clt.service.edu.service.CourseCollectService;
import com.clt.service.edu.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;
    @Resource
    private OssFileService ossFileService;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CourseCollectMapper courseCollectMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CourseCollectService courseCollectService;
    @Resource
    private Redisson redisson;

    private ValueOperations<String, String> opsForValue;

    private static final String COURSE_VIEW_COUNT_KEY = "Course_View_Count_CourseId:";
    private static final String COURSE_FOR_REDIS_COURSEID_KEY = "CourseForRedis_CourseId:";

    // 课程购买总数key
    private static final String COURSE_BUY_COUNT = "Course_Buy_Count_CourseId:";


    @PostConstruct
    private void init() {
        opsForValue = redisTemplate.opsForValue();
    }



    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {

        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        course.setStatus(Course.COURSE_DRAFT);
        baseMapper.insert(course);

        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.insert(courseDescription);

        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoById(String id) {
        return this.baseMapper.getCourseInfoById(id);
    }

    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfoForm) {
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        baseMapper.updateById(course);

        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(courseInfoForm.getId());
        redisTemplate.delete(COURSE_FOR_REDIS_COURSEID_KEY + courseInfoForm.getId());
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo) {

        //组装查询条件
        QueryWrapper<CourseVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("c.gmt_create");

        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();

        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("c.title", title);
        }

        if (!StringUtils.isEmpty(teacherId)) {
            queryWrapper.eq("c.teacher_id", teacherId);
        }

        if (!StringUtils.isEmpty(subjectParentId)) {
            queryWrapper.eq("c.subject_parent_id", subjectParentId);
        }

        if (!StringUtils.isEmpty(subjectId)) {
            queryWrapper.eq("c.subject_id", subjectId);
        }

        //组装分页
        Page<CourseVo> pageParam = new Page<>(page, limit);

        //执行分页查询
        //只需要在mapper层传入封装好的分页组件即可，sql分页条件组装的过程由mp自动完成
        List<CourseVo> records = baseMapper.selectPageByCourseQueryVo(pageParam, queryWrapper);
        //将records设置到pageParam中
        return pageParam.setRecords(records);
    }

    @Override
    public boolean removeCoverById(String id) {
        Course course = baseMapper.selectById(id);
        if (Objects.nonNull(course)) {
            String cover = course.getCover();
            if (!StringUtils.isEmpty(cover)) {
                R r = ossFileService.removeFile(cover);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCourseById(String id) {
        //根据courseId删除Video(课时)
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq(VideoEnum.COURSE_ID.getColumn(), id);
        videoMapper.delete(videoQueryWrapper);

        //根据courseId删除Chapter(章节)
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq(ChapterEnum.COURSE_ID.getColumn(), id);
        chapterMapper.delete(chapterQueryWrapper);

        //根据courseId删除Comment(评论)
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq(CommentEnum.COURSE_ID.getColumn(), id);
        commentMapper.delete(commentQueryWrapper);

        //根据courseId删除CourseCollect(课程收藏)
        QueryWrapper<CourseCollect> courseCollectQueryWrapper = new QueryWrapper<>();
        courseCollectQueryWrapper.eq(CourseCollectEnum.COURSE_ID.getColumn(), id);
        courseCollectMapper.delete(courseCollectQueryWrapper);

        //根据courseId删除CourseDescription(课程详情)
        courseDescriptionMapper.deleteById(id);

        //删除课程
        return this.removeById(id);
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {

        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {

        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        return this.updateById(course);
    }

    @Override
    public List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo) {

        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();

        //查询已发布的课程
        queryWrapper.eq(CourseEnum.STATUS.getColumn(), Course.COURSE_NORMAL);

        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectParentId())) {
            queryWrapper.eq(CourseEnum.SUBJECT_PARENT_ID.getColumn(), webCourseQueryVo.getSubjectParentId());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getSubjectId())) {
            queryWrapper.eq(CourseEnum.SUBJECT_ID.getColumn(), webCourseQueryVo.getSubjectId());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getBuyCountSort())) {
            queryWrapper.orderByDesc(CourseEnum.BUY_COUNT.getColumn());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getGmtCreateSort())) {
            queryWrapper.orderByDesc(BaseEnum.GMT_CREATE.getColumn());
        }

        if (!StringUtils.isEmpty(webCourseQueryVo.getPriceSort())) {
            if (webCourseQueryVo.getType() == null || webCourseQueryVo.getType() == 1) {
                queryWrapper.orderByAsc(CourseEnum.PRICE.getColumn());
            } else {
                queryWrapper.orderByDesc(CourseEnum.PRICE.getColumn());
            }
        }

        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public WebCourseVo selectWebCourseVoById(String courseId, String userId) {
        if (StringUtils.isEmpty(courseId)) {
            return null;
        }

        // 先去redis中查是否有相关课程的缓存
        String CourseForRedisJson = opsForValue.get(COURSE_FOR_REDIS_COURSEID_KEY + courseId);
        CourseForRedis courseForRedis = JSON.parseObject(CourseForRedisJson, CourseForRedis.class);
        WebCourseVo webCourseVo;
        if (Objects.isNull(courseForRedis)) {
            // 如果没有相关缓存，就去数据库查并重新缓存到redis中
            webCourseVo = baseMapper.selectWebCourseVoById(courseId);
            if (Objects.isNull(webCourseVo)) {
                return null;
            }
            courseForRedis = new CourseForRedis();
            BeanUtils.copyProperties(webCourseVo, courseForRedis);
            opsForValue.set(COURSE_FOR_REDIS_COURSEID_KEY + courseId, JSON.toJSONString(courseForRedis), 24, TimeUnit.HOURS);
        } else {
            webCourseVo = new WebCourseVo();
            BeanUtils.copyProperties(courseForRedis, webCourseVo);
        }

        //更新浏览数
        Long viewCount = redisTemplate.opsForValue().increment(COURSE_VIEW_COUNT_KEY + courseId);
        webCourseVo.setViewCount(viewCount);
        assert viewCount != null;
        if (viewCount % 10 == 0) {
            Course course = new Course();
            course.setId(courseId);
            course.setViewCount(viewCount);
            baseMapper.updateById(course);
        }

        // 如果请求中携带token表示用户已经登录
        if (!StringUtils.isEmpty(userId)) {
            webCourseVo.setLike(courseCollectService.isLike(courseId, userId));
            webCourseVo.setCollect(courseCollectService.isCollect(courseId, userId));
        }

        // 填充点赞总数
        webCourseVo.setBuyCount(Long.parseLong(courseCollectService.getCourseBuyCount(courseId)));

        // 填充课程收藏和喜欢总数
        webCourseVo.setLikeCount(courseCollectService.getCourseLikeCount(courseId));
        webCourseVo.setCollectCount(courseCollectService.getCourseCollectCount(courseId));


        //返回课程相关信息
        return webCourseVo;
    }

    @Cacheable(value = "index", key = "'selectHotCourse'")
    @Override
    public List<Course> selectHotCourse() {

        QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
        //查询已发布的课程
        courseQueryWrapper.eq(CourseEnum.STATUS.getColumn(), Course.COURSE_NORMAL);
        courseQueryWrapper.orderByDesc(CourseEnum.VIEW_COUNT.getColumn());
        courseQueryWrapper.last("limit 8");

        return baseMapper.selectList(courseQueryWrapper);
    }

    @Override
    public CourseDto getCourseDtoById(String courseId) {
        return baseMapper.selectCourseDtoById(courseId);
    }

    @Override
    public void updateBuyCountByCourseId(String courseId) {

        Long increment = opsForValue.increment(COURSE_BUY_COUNT + courseId);

        assert increment != null;
//        if (increment % 3 == 0) {
            UpdateWrapper<Course> courseUpdateWrapper = new UpdateWrapper<>();
            courseUpdateWrapper.eq(BaseEnum.ID.getColumn(), courseId);
            courseUpdateWrapper.set(CourseEnum.BUY_COUNT.getColumn(), increment);
            baseMapper.update(null, courseUpdateWrapper);
//        }
    }

}
