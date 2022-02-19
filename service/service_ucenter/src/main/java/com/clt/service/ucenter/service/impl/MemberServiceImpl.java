package com.clt.service.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.FormUtils;
import com.clt.common.base.util.JwtInfo;
import com.clt.common.base.util.JwtUtils;
import com.clt.common.base.util.MD5;
import com.clt.service.base.dto.MemberDto;
import com.clt.service.base.exception.MyException;
import com.clt.service.ucenter.entity.Member;
import com.clt.service.ucenter.entity.vo.LoginVo;
import com.clt.service.ucenter.entity.vo.RegisterVo;
import com.clt.service.ucenter.enums.MemberEnum;
import com.clt.service.ucenter.mapper.MemberMapper;
import com.clt.service.ucenter.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

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
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MemberEnum.MOBILE.getColumn(), mobile);
        Integer count = baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new MyException(ResultCodeEnum.REGISTER_MOBLE_ERROR);
        }

        //注册
        Member member = new Member();
        member.setNickname(nickname);
        member.setMobile(mobile);
        member.setPassword(MD5.encrypt(password));
        member.setAvatar("https://thirdwx.qlogo.cn/mmopen/vi_32/FBickBicLQIrov5BT9jNUYBnPzMNibIkmfcOczxySDYn8psZQ9fgoLF6V9zuonicvaG6AEYPFfSefW7Tc8uB1ykWzQ/132");
        member.setIsDisabled(false);
        baseMapper.insert(member);
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
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MemberEnum.MOBILE.getColumn(), mobile);
        Member member = baseMapper.selectOne(queryWrapper);
        if(member == null){
            throw new MyException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }

        //校验密码是否正确
        if(!MD5.encrypt(password).equals(member.getPassword())){
            throw new MyException(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        //校验用户是否被禁用
        if(member.getIsDisabled()){
            throw new MyException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //登录：生成token
        JwtInfo info = new JwtInfo();
        info.setId(member.getId());
        info.setNickname(member.getNickname());
        info.setAvatar(member.getAvatar());

        String jwtToken = JwtUtils.getJwtToken(info, 1800);

        return jwtToken;
    }

    @Override
    public Member getByOpenid(String openid) {

        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq(MemberEnum.OPENID.getColumn(), openid);
        return baseMapper.selectOne(memberQueryWrapper);
    }

    @Override
    public MemberDto getMemberDtoByMemberId(String memberId) {

        Member member = baseMapper.selectById(memberId);
        MemberDto memberDto = new MemberDto();
        BeanUtils.copyProperties(member, memberDto);
        return memberDto;
    }

    @Override
    public Integer countRegisterNum(String day) {
        return baseMapper.selectRegisterNumByDay(day);
    }
}
