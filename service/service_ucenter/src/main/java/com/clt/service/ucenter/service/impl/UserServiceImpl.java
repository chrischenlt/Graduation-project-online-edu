package com.clt.service.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clt.common.base.enums.BaseEnum;
import com.clt.common.base.result.ResultCodeEnum;
import com.clt.common.base.util.*;
import com.clt.service.base.dto.UserDto;
import com.clt.service.base.exception.MyException;
import com.clt.service.ucenter.entity.User;
import com.clt.service.ucenter.entity.vo.LoginVo;
import com.clt.service.ucenter.entity.vo.RegisterVo;
import com.clt.service.ucenter.entity.vo.UserQueryVo;
import com.clt.service.ucenter.enums.UserEnum;
import com.clt.service.ucenter.mapper.UserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clt.service.ucenter.service.UserService;
import com.clt.service.ucenter.util.UcenterProperties;
import com.google.gson.Gson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.util.calendar.BaseCalendar;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

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
    @Autowired
    private UcenterProperties ucenterProperties;

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

        // 用户账号验证成功，对bitmap进行对应操作
        redisTemplate.opsForValue().setBit("Login_Time_UserId:" + user.getId(), LocalDate.now().getDayOfYear(), true);

        //登录：生成token
        JwtInfo info = new JwtInfo();
        info.setId(user.getId());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatar());

        String jwtToken = JwtUtils.getJwtToken(info, 7200);

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

    @Override
    public String loginCallBackWithWechat(String code, String state, HttpSession session) {
        if(StringUtils.isEmpty(code) || StringUtils.isEmpty(state)){
            log.error("非法回调请求");
            throw new MyException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        String sessionState = (String)session.getAttribute("wx_open_state");
        if(!state.equals(sessionState)){
            log.error("非法回调请求");
            throw new MyException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }


        //携带code临时票据，和appid以及appsecret请求access_token和openid（微信唯一标识）
        String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        //组装参数：?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        Map<String, String> accessTokenParam = new HashMap<>();
        accessTokenParam.put("appid", ucenterProperties.getAppId());
        accessTokenParam.put("secret", ucenterProperties.getAppSecret());
        accessTokenParam.put("code", code);
        accessTokenParam.put("grant_type", "authorization_code");
        HttpClientUtils client = new HttpClientUtils(accessTokenUrl, accessTokenParam);

        String result = "";
        try {
            //发送请求：组装完整的url字符串、发送请求
            client.get();
            //得到响应
            result = client.getContent();
            System.out.println("result = " + result);
        } catch (Exception e) {
            log.error("获取access_token失败");
            throw new MyException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        Gson gson = new Gson();
        HashMap<String, Object> resultMap = gson.fromJson(result, HashMap.class);

        //失败的响应结果
        Object errcodeObj = resultMap.get("errcode");
        if(errcodeObj != null){
            Double errcode = (Double)errcodeObj;
            String errmsg = (String)resultMap.get("errmsg");
            log.error("获取access_token失败：" + "code：" + errcode + ", message：" +  errmsg);
            throw new MyException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        //解析出结果中的access_token和openid
        String accessToken = (String)resultMap.get("access_token");
        String openid = (String)resultMap.get("openid");

        System.out.println("accessToken:" + accessToken);
        System.out.println("openid:" + openid);

        //在本地数据库中查找当前微信用户的信息
        User user = this.getByOpenid(openid);

        if(user == null){
            //if：如果当前用户不存在，则去微信的资源服务器获取用户个人信息（携带access_token）
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo";
            //组装参数：?access_token=ACCESS_TOKEN&openid=OPENID
            Map<String, String> baseUserInfoParam = new HashMap<>();
            baseUserInfoParam.put("access_token", accessToken);
            baseUserInfoParam.put("openid", openid);
            client = new HttpClientUtils(baseUserInfoUrl, baseUserInfoParam);

            String resultUserInfo = "";
            try {
                client.get();
                resultUserInfo = client.getContent();
            } catch (Exception e) {
                log.error(ExceptionUtils.getMessage(e));
                throw new MyException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            HashMap<String, Object> resultUserInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
            //失败的响应结果
            errcodeObj = resultUserInfoMap.get("errcode");
            if(errcodeObj != null){
                Double errcode = (Double)errcodeObj;
                String errmsg = (String)resultMap.get("errmsg");
                log.error("获取用户信息失败：" + "code：" + errcode + ", message：" +  errmsg);
                throw new MyException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            //解析出结果中的用户个人信息
            String nickname = (String)resultUserInfoMap.get("nickname");
            String avatar = (String)resultUserInfoMap.get("headimgurl");
            Double sex = (Double)resultUserInfoMap.get("sex");

            //在本地数据库中插入当前微信用户的信息（使用微信账号在本地服务器注册新用户）
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setSex(sex.intValue());
            user.setIsDisabled(false);
            this.save(user);
        }


        // 校验用户是否被禁用
        if (user.getIsDisabled()) {
            throw new MyException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // 用户账号验证成功，对bitmap进行对应操作
        redisTemplate.opsForValue().setBit("Login_Time_UserId:" + user.getId(), LocalDate.now().getDayOfYear(), true);

        //则直接使用当前用户的信息登录（生成jwt）
        //member =>Jwt
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setId(user.getId());
        jwtInfo.setNickname(user.getNickname());
        jwtInfo.setAvatar(user.getAvatar());
        String jwtToken = JwtUtils.getJwtToken(jwtInfo, 7200);

        return "redirect:http://localhost:3000?token=" + jwtToken;
    }

    @Override
    public Long getUserLoginTime(String userId) {
        RedisCacheUtils redisCacheUtils = new RedisCacheUtils();

        return redisCacheUtils.bitCount(redisTemplate, "Login_Time_UserId:" + userId);
    }

    @Override
    public IPage<User> selectPage(Long page, Long limit, UserQueryVo userQueryVo) {

        //组装查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(BaseEnum.GMT_CREATE.getColumn());

        String mobile = userQueryVo.getMobile();
        String nickname = userQueryVo.getNickname();

        if (!StringUtils.isEmpty(mobile)) {
            queryWrapper.like(UserEnum.MOBILE.getColumn(), mobile);
        }

        if (!StringUtils.isEmpty(nickname)) {
            queryWrapper.eq(UserEnum.NICKNAME.getColumn(), nickname);
        }

        //组装分页
        Page<User> pageParam = new Page<>(page, limit);

        return baseMapper.selectPage(pageParam, queryWrapper);
    }
}
