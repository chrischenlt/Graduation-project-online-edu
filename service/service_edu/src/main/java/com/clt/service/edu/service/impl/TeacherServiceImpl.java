package com.clt.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.common.base.result.R;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.entity.vo.TeacherQueryVo;
import com.clt.service.edu.enums.TeacherEnum;
import com.clt.service.edu.feign.OssFileService;
import com.clt.service.edu.mapper.TeacherMapper;
import com.clt.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private OssFileService ossFileService;

    @Override
    public IPage<Teacher> selectPage(Page<Teacher> pageParam, TeacherQueryVo teacherQueryVo) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(TeacherEnum.SORT.getColumn());
        if (teacherQueryVo == null) {
            return baseMapper.selectPage(pageParam, queryWrapper);
        }

        if (!StringUtils.isEmpty(teacherQueryVo.getName())) {
            queryWrapper.likeRight(TeacherEnum.NAME.getColumn(), teacherQueryVo.getName());
        }

        if (Objects.nonNull(teacherQueryVo.getLevel())) {
            queryWrapper.eq(TeacherEnum.LEVEL.getColumn(), teacherQueryVo.getLevel());
        }

        if (!StringUtils.isEmpty(teacherQueryVo.getJoinDateBegin())) {
            queryWrapper.ge(TeacherEnum.JOIN_DATE.getColumn(), teacherQueryVo.getJoinDateBegin());
        }

        if (!StringUtils.isEmpty(teacherQueryVo.getJoinDateEnd())) {
            queryWrapper.le(TeacherEnum.JOIN_DATE.getColumn(), teacherQueryVo.getJoinDateEnd());
        }

        return baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public List<Map<String, Object>> selectNameList(String key) {

        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name");
        queryWrapper.likeRight("name", key);

        return baseMapper.selectMaps(queryWrapper);
    }

    @Override
    public boolean removeAvatarById(String id) {

        //根据id获取讲师的avatar的url
        Teacher teacher = baseMapper.selectById(id);
        if (Objects.isNull(teacher)) {
            return false;
        }
        String avatar = teacher.getAvatar();
        if (!StringUtils.isEmpty(avatar)) {
            R r = ossFileService.removeFile(avatar);
            return r.getSuccess();
        }
        return false;
    }
}
