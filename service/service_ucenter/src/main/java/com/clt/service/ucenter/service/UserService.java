package com.clt.service.ucenter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clt.service.base.dto.UserDto;
import com.clt.service.ucenter.entity.User;
import com.clt.service.ucenter.entity.vo.LoginVo;
import com.clt.service.ucenter.entity.vo.RegisterVo;
import com.clt.service.ucenter.entity.vo.UserQueryVo;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author chenlt
 * @since 2022-02-06
 */
public interface UserService extends IService<User> {

    void register(RegisterVo registerVo);

    String login(LoginVo loginVo);

    User getByOpenid(String openid);

    UserDto getUserDtoByUserId(String memberId);

    Integer countRegisterNum(String day);

    String loginCallBackWithWechat(String code, String state, HttpSession session);

    Long getUserLoginTime(String userId);

    IPage<User> selectPage(Long page, Long limit, UserQueryVo userQueryVo);

    void forbiddenUserAccount(String userId);

    void enableUserAccount(String userId);
}
