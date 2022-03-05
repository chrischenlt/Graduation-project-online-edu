package com.clt.service.ucenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clt.service.ucenter.entity.User;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author chenlt
 * @since 2022-02-06
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    Integer selectRegisterNumByDay(String day);
}
