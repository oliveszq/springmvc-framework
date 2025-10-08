package com.example.framework.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.framework.constant.CommonConstant;
import com.example.framework.entity.UserAuth;
import com.example.framework.entity.UserInfo;
import com.example.framework.entity.UserRole;
import com.example.framework.enums.LoginTypeEnum;
import com.example.framework.enums.RoleEnum;
import com.example.framework.exception.BizException;
import com.example.framework.mapper.UserAuthMapper;
import com.example.framework.mapper.UserInfoMapper;
import com.example.framework.mapper.UserRoleMapper;
import com.example.framework.model.dto.*;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.PasswordVO;
import com.example.framework.model.vo.QQLoginVO;
import com.example.framework.model.vo.UserVO;
import com.example.framework.service.*;
import com.example.framework.utils.CommonUtil;
import com.example.framework.utils.PageUtil;
import com.example.framework.utils.UserUtil;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.framework.constant.RabbitMQConstant.EMAIL_EXCHANGE;
import static com.example.framework.constant.RedisConstant.*;
import static com.example.framework.enums.UserAreaTypeEnum.getUserAreaType;
import static com.example.framework.utils.CommonUtil.checkEmail;

/**
 * @Description:
 * @Author: QLong
 * @Date: 2023/3/15
 **/
@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public void sendCode(String username) {
        if (!checkEmail(username)){
            throw new BizException("请输入正确的邮箱");
        }
        String code = CommonUtil.getRandomCode();

        Map<String, Object> map = new HashMap<>();
        map.put("content", "您的验证码为 " + code + " 有效期15分钟，请不要告诉他人哦！");
        map.put("label", label());
        EmailDTO emailDTO = EmailDTO.builder()
                .email(username)
                .subject("验证码")
                .template("common.html")
                .commentMap(map)
                .build();
        /**
         * 1、异步处理：RabbitMQ通过消息队列实现异步处理，将发送邮件的请求转换成消息，并发送到消息队列中。
         *    这样可以将发送邮件的过程与主应用解耦，提高系统的可伸缩性和稳定性。而传统的JavaMailSender是同步的方式，
         *        需要等待邮件发送完成才能继续执行后续操作。
         *
         * 2、可靠性：RabbitMQ具有高度可靠性和消息持久化的特性。即使在消息发送过程中出现异常或故障，
         *    消息也可以得到保证，并且可以配置消息的持久化，确保消息不会因为重启或其他原因丢失。
         *        而传统的JavaMailSender在发送邮件过程中可能会受到网络波动、邮件服务器故障等因素的影响，导致邮件发送失败。
         *
         * 3、可扩展性：通过使用RabbitMQ，可以轻松地构建分布式系统，将不同的服务和模块进行解耦，
         *    实现水平扩展。通过增加消息消费者，可以处理更多的邮件发送请求，提高系统的吞吐量。
         *      而传统的JavaMailSender通常是在同一个应用内发送邮件，不容易进行横向扩展。
         *
         *  综上所述，使用RabbitMQ的RabbitTemplate可以提供异步、可靠和可扩展的邮箱信息发送能力，
         *      而传统的JavaMailSender则更适用于简单的同步邮件发送操作。
         */
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*",
                new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        redisService.set(USER_CODE_KEY + username, code, CODE_EXPIRE_TIME);
    }

    public LabelDTO label() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("token","LwExDtUWhF3rH5ib");
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);

        // 构建请求的URL
        UriComponents builder = UriComponentsBuilder.fromHttpUrl("https://v2.alapi.cn/api/mingyan")
                .build();
        // 发送请求并获取响应
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class
        );
        LabelDTO labelDTO = null;
        JSONObject jsonObject = JSON.parseObject(responseEntity.getBody());
        if (jsonObject.getInteger("code") == 200) {
            JSONObject data = jsonObject.getJSONObject("data");
            String content = data.getString("content");
            String author = data.getString("author");
            labelDTO =  LabelDTO.builder().content(content).author(author).build();
        }
        return labelDTO;
    }

    @Override
    public void register(UserVO userVO) {
        if (!checkEmail(userVO.getUsername())) {
            throw new BizException("邮箱格式不对!");
        }
        if (checkUser(userVO)) {
            throw new BizException("邮箱已被注册！");
        }
        UserInfo userInfo = UserInfo.builder()
                .email(userVO.getUsername())
                //IdWorker.getId()用于生成唯一标识，使用了雪花算法，是一种高效的分布式ID生成算法
                .nickname(CommonConstant.DEFAULT_NICKNAME + IdWorker.getId())
                .build();
        userInfoMapper.insert(userInfo);

        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);

        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(userVO.getUsername())
                //BCrypt.gensalt():用于生成一个随机的盐值（salt）
                //可以将用户输入的密码以及生成的随机盐值一起传入 BCrypt.hashpw 方法中进行哈希计算，输出一个经过加密的密码串。
                .password(BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthMapper.insert(userAuth);
    }

    @Override
    public UserLogoutStatusDTO logout() {
        tokenService.delLoginUser(UserUtil.getUserDetailsDTO().getId());
        return new UserLogoutStatusDTO("注销成功");
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAreaDTO> listUserAreas(ConditionVO conditionVO) {
        List<UserAreaDTO> userAreaDTOs = new ArrayList<>();
        switch (Objects.requireNonNull(getUserAreaType(conditionVO.getType()))){
            case USER:
                Object userArea = redisService.get(USER_AREA);
                if (Objects.nonNull(userArea)){
                    userAreaDTOs =  JSON.parseObject(userArea.toString(), List.class);
                }
                return userAreaDTOs;
            case VISITOR:
                Map<String, Object> visitorArea = redisService.hGetAll(VISITOR_AREA);
                if (Objects.nonNull(visitorArea)){
                    userAreaDTOs = visitorArea.entrySet().stream()
                            .map(item -> UserAreaDTO.builder()
                                    .name(item.getKey())
                                    .value(Long.valueOf(item.getValue().toString()))
                                    .build())
                            .collect(Collectors.toList());
                }
                return userAreaDTOs;
            default:
                break;
        }
        return userAreaDTOs;
    }

    @Override
    public PageResultDTO<UserAdminDTO> listUsers(ConditionVO conditionVO) {
        Integer count = userAuthMapper.countUser(conditionVO);
        if (count == 0){
            return new PageResultDTO<>();
        }
        List<UserAdminDTO> userAdminDTOS = userAuthMapper.listUsers(PageUtil.getLimitCurrent(),
                PageUtil.getSize(), conditionVO);
        return new PageResultDTO<>(userAdminDTOS, count);
    }

    private Boolean checkUser(UserVO user) {
        if (!user.getCode().equals(redisService.get(USER_CODE_KEY + user.getUsername()))) {
            throw new BizException("验证码错误！");
        }
        UserAuth userAuth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUsername, user.getUsername()));
        return Objects.nonNull(userAuth);
    }

    @Override
    public void updatePassword(UserVO userVO) {
        if (!checkUser(userVO)){
            throw new BizException("邮箱尚未注册！");
        }
        userAuthMapper.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword, BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .eq(UserAuth::getUsername, userVO.getUsername()));
    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        UserAuth userAuth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getUserInfoId, UserUtil.getUserDetailsDTO().getId()));
        if (Objects.isNull(userAuth) || !BCrypt.checkpw(passwordVO.getOldPassword(), userAuth.getPassword())){
            throw new BizException("旧密码错误！");
        }
        userAuthMapper.updateById(UserAuth.builder()
                .id(userAuth.getId())
                .userInfoId(UserUtil.getUserDetailsDTO().getId())
                .password(BCrypt.hashpw(passwordVO.getNewPassword(), BCrypt.gensalt()))
                .build());
    }

    @Override
    public UserInfoDTO qqLogin(QQLoginVO qqLoginVO) {
        return null;
    }
}
