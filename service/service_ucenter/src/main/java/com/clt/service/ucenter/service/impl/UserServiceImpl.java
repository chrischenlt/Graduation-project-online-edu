package com.clt.service.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.FormUtils;
import com.clt.common.base.util.JwtInfo;
import com.clt.common.base.util.JwtUtils;
import com.clt.common.base.util.MD5;
import com.clt.service.base.dto.UserDto;
import com.clt.service.base.exception.MyException;
import com.clt.service.ucenter.entity.User;
import com.clt.service.ucenter.entity.vo.LoginVo;
import com.clt.service.ucenter.entity.vo.RegisterVo;
import com.clt.service.ucenter.enums.UserEnum;
import com.clt.service.ucenter.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clt.service.ucenter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author chenlt
 * @since 2022-02-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void register(RegisterVo registerVo) {

        //校验参数
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        if(StringUtils.isEmpty(mobile)
                || !FormUtils.isMobile(mobile)){
            throw new MyException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }

        if(StringUtils.isEmpty(nickname)
                || StringUtils.isEmpty(password)
                || StringUtils.isEmpty(code)){
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验验证码：redis
        String checkCode = (String)redisTemplate.opsForValue().get(mobile);
        if(!code.equals(checkCode)){
            throw new MyException(ResultCodeEnum.CODE_ERROR);
        }

        //用户是否注册：mysql
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(UserEnum.MOBILE.getColumn(), mobile);
        Integer count = baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new MyException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }

        //注册
        User user = new User();
        user.setNickname(nickname);
        user.setMobile(mobile);
        user.setPassword(MD5.encrypt(password));
        user.setAvatar("https://thirdwx.qlogo.cn/mmopen/vi_32/FBickBicLQIrov5BT9jNUYBnPzMNibIkmfcOczxySDYn8psZQ9fgoLF6V9zuonicvaG6AEYPFfSefW7Tc8uB1ykWzQ/132");
        user.setIsDisabled(false);
        baseMapper.insert(user);
    }

    @Override
    public String login(LoginVo loginVo) {

        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //校验：参数是否合法
        if(StringUtils.isEmpty(mobile)
                || !FormUtils.isMobile(mobile)
                || StringUtils.isEmpty(password)){
            throw new MyException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验手机号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(UserEnum.MOBILE.getColumn(), mobile);
        User user = baseMapper.selectOne(queryWrapper);
        if(user == null){
            throw new MyException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }

        //校验密码是否正确
        if(!MD5.encrypt(password).equals(user.getPassword())){
            throw new MyException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        //校验用户是否被禁用
        if(user.getIsDisabled()){
            throw new MyException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //登录：生成token
        JwtInfo info = new JwtInfo();
        info.setId(user.getId());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatar());

        String jwtToken = JwtUtils.getJwtToken(info, 1800);

        return jwtToken;
    }

    @Override
    public User getByOpenid(String openid) {

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq(UserEnum.OPENID.getColumn(), openid);
        return baseMapper.selectOne(userQueryWrapper);
    }

    @Override
    public UserDto getUserDtoByUserId(String userId) {

        User user = baseMapper.selectById(userId);
        UserDto UserDto = new UserDto();
        BeanUtils.copyProperties(user, UserDto);
        return UserDto;
    }

    @Override
    public Integer countRegisterNum(String day) {
        return baseMapper.selectRegisterNumByDay(day);
    }
}
