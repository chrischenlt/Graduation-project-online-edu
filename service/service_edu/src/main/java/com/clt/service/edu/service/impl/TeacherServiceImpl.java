package com.clt.service.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.service.edu.entity.Teacher;
import com.clt.service.edu.entity.vo.TeacherQueryVo;
import com.clt.service.edu.enums.TeacherEnum;
import com.clt.service.edu.mapper.TeacherMapper;
import com.clt.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
}
