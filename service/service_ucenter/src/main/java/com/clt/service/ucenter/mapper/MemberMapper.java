package com.clt.service.ucenter.mapper;

import com.clt.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface MemberMapper extends BaseMapper<Member> {

    Integer selectRegisterNumByDay(String day);
}
