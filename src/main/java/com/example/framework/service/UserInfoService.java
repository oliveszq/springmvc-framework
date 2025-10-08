package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.UserInfo;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.dto.UserInfoDTO;
import com.example.framework.model.dto.UserOnlineDTO;
import com.example.framework.model.vo.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService extends IService<UserInfo> {
    void updateUserDisable(UserDisableVO userDisableVO);

    void updateUserRole(UserRoleVO userRoleVO);

    PageResultDTO<UserOnlineDTO> listOnlineUsers(ConditionVO conditionVO);

    void removeOnlineUser(Integer userInfoId);

    void updateUserSubscribe(SubscribeVO subscribeVO);

    void updateUserInfo(UserInfoVO userInfoVO);

    UserInfoDTO getUserInfoById(Integer userInfoId);

    void saveUserEmail(EmailVO emailVO);

    String updateUserAvatar(MultipartFile file);
}
