package com.clt.service.edu.service.impl;

import com.clt.service.edu.entity.Comment;
import com.clt.service.edu.mapper.CommentMapper;
import com.clt.service.edu.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-01-06
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
