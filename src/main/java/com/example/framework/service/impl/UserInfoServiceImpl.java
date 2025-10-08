package com.example.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.framework.entity.UserAuth;
import com.example.framework.entity.UserInfo;
import com.example.framework.entity.UserRole;
import com.example.framework.enums.FilePathEnum;
import com.example.framework.exception.BizException;
import com.example.framework.mapper.UserAuthMapper;
import com.example.framework.mapper.UserInfoMapper;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.dto.UserDetailsDTO;
import com.example.framework.model.dto.UserInfoDTO;
import com.example.framework.model.dto.UserOnlineDTO;
import com.example.framework.model.vo.*;
import com.example.framework.service.*;
import com.example.framework.strategy.context.UploadStrategyContext;
import com.example.framework.utils.BeanCopyUtil;
import com.example.framework.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.framework.constant.RedisConstant.USER_CODE_KEY;
import static com.example.framework.utils.PageUtil.*;

/**
 * @author: qinglong
 * @create-date: 2023/10/16 17:28
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserAuthMapper userAuthMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UploadStrategyContext uploadStrategyContext;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserDisable(UserDisableVO userDisableVO) {
        UserInfo userInfo = UserInfo.builder()
                .id(userDisableVO.getId())
                .isDisable(userDisableVO.getIsDisable())
                .build();
        userInfoMapper.updateById(userInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserRole(UserRoleVO userRoleVO) {
        UserInfo userInfo = UserInfo.builder()
                .id(userRoleVO.getUserInfoId())
                .nickname(userRoleVO.getNickname())
                .build();
        userInfoMapper.updateById(userInfo);
        userRoleService.remove(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userRoleVO.getUserInfoId()));
        List<UserRole> userRoles = userRoleVO.getRoleIds().stream().map(roleId -> UserRole.builder()
                .userId(userRoleVO.getUserInfoId())
                .roleId(roleId)
                .build()).collect(Collectors.toList());
        userRoleService.saveBatch(userRoles);
    }

    @Override
    public PageResultDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO) {
        Map<String, Object> userMaps = redisService.hGetAll("login_user");
        Collection<Object> values = userMaps.values();
        ArrayList<UserDetailsDTO> userDetailsDTOs = new ArrayList<>();
        for(Object value : values){
            userDetailsDTOs.add((UserDetailsDTO) value);
        }
        List<UserOnlineDTO> userOnlineDTOs = BeanCopyUtil.copyList(userDetailsDTOs, UserOnlineDTO.class);
        List<UserOnlineDTO> onlineUsers = userOnlineDTOs.stream()
                .filter(item -> StringUtils.isBlank(conditionVO.getKeywords()) || item.getNickname().contains(conditionVO.getKeywords()))
                .sorted(Comparator.comparing(UserOnlineDTO::getLastLoginTime).reversed())
                .collect(Collectors.toList());
        int fromIndex = getLimitCurrent().intValue();
        int size = getSize().intValue();
        int toIndex = onlineUsers.size() - fromIndex > size ? fromIndex + size : onlineUsers.size();
        List<UserOnlineDTO> userOnlineList = onlineUsers.subList(fromIndex, toIndex);
        return new PageResultDTO<>(userOnlineList, onlineUsers.size());
    }

    @Override
    public void removeOnlineUser(Integer userInfoId) {
        Integer userId = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getUserInfoId, userInfoId)).getId();
        tokenService.delLoginUser(userId);
    }

    @Override
    public void updateUserSubscribe(SubscribeVO subscribeVO) {
        UserInfo temp = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, subscribeVO.getUserId()));
        if (StringUtils.isEmpty(temp.getEmail())){
            throw new BizException("邮箱未绑定！");
        }
        UserInfo userInfo = UserInfo.builder()
                .id(subscribeVO.getUserId())
                .isSubscribe(subscribeVO.getIsSubscribe())
                .build();
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public void updateUserInfo(UserInfoVO userInfoVO) {
        UserInfo userInfo = BeanCopyUtil.copyObject(userInfoVO, UserInfo.class);
        userInfo.setId(UserUtil.getUserDetailsDTO().getUserInfoId());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfoDTO getUserInfoById(Integer userInfoId) {
        UserInfo userInfo = userInfoMapper.selectById(userInfoId);
        return BeanCopyUtil.copyObject(userInfo, UserInfoDTO.class);
    }

    @Override
    public void saveUserEmail(EmailVO emailVO) {
        if (Objects.isNull(redisService.get(USER_CODE_KEY + emailVO.getEmail()))) {
            throw new BizException("验证码错误");
        }
        if (!emailVO.getCode().equals(redisService.get(USER_CODE_KEY + emailVO.getEmail()).toString())) {
            throw new BizException("验证码错误！");
        }
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getUserDetailsDTO().getUserInfoId())
                .email(emailVO.getEmail())
                .build();
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public String updateUserAvatar(MultipartFile file) {
        String avatar = uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.AVATAR.getPath());
        UserInfo userInfo = UserInfo.builder()
                .id(UserUtil.getUserDetailsDTO().getUserInfoId())
                .avatar(avatar)
                .build();
        userInfoMapper.updateById(userInfo);
        return avatar;
    }
}
