package com.clt.service.ucenter.service;

import com.clt.service.base.dto.MemberDto;
import com.clt.service.ucenter.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clt.service.ucenter.entity.vo.LoginVo;
import com.clt.service.ucenter.entity.vo.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author chenlt
 * @since 2022-02-06
 */
public interface MemberService extends IService<Member> {

    void register(RegisterVo registerVo);

    String login(LoginVo loginVo);

    Member getByOpenid(String openid);

    MemberDto getMemberDtoByMemberId(String memberId);

    Integer countRegisterNum(String day);
}
