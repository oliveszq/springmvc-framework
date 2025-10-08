package com.example.framework.service;

import com.example.framework.model.dto.*;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.PasswordVO;
import com.example.framework.model.vo.QQLoginVO;
import com.example.framework.model.vo.UserVO;

import java.util.List;

public interface UserAuthService{
    void sendCode(String username);

    void register(UserVO userVO);

    UserLogoutStatusDTO logout();

    List<UserAreaDTO> listUserAreas(ConditionVO conditionVO);

    PageResultDTO<UserAdminDTO> listUsers(ConditionVO conditionVO);
    void updatePassword(UserVO user);
    void updateAdminPassword(PasswordVO passwordVO);


    UserInfoDTO qqLogin(QQLoginVO qqLoginVO);

}
