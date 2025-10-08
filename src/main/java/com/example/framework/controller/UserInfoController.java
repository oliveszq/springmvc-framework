package com.example.framework.controller;

import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.dto.UserInfoDTO;
import com.example.framework.model.dto.UserOnlineDTO;
import com.example.framework.model.vo.*;
import com.example.framework.service.UserInfoService;
import com.example.framework.utils.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.framework.constant.OptTypeConstant.DELETE;
import static com.example.framework.constant.OptTypeConstant.UPDATE;

/**
 * @author: qinglong
 * @create-date: 2024/1/17 18:33
 */
@Tag(name = "用户信息模块")
@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @OptLog(optType = UPDATE)
    @Operation(summary = "更新用户信息")
    @PutMapping("/users/info")
    public ResultVO<?> updateUserInfo(@Valid @RequestBody UserInfoVO userInfoVO) {
        userInfoService.updateUserInfo(userInfoVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "更新用户头像")
    @Parameter(name = "file", description = "用户头像", required = true, schema = @Schema(type = "MultipartFile") )
    @PostMapping("/users/avatar")
    public ResultVO<String> updateUserAvatar(MultipartFile file) {
        return ResultVO.ok(userInfoService.updateUserAvatar(file));
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "绑定用户邮箱")
    @PutMapping("/users/email")
    public ResultVO<?> saveUserEmail(@Valid @RequestBody EmailVO emailVO) {
        userInfoService.saveUserEmail(emailVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改用户的订阅状态")
    @PutMapping("/users/subscribe")
    public ResultVO<?> updateUserSubscribe(@RequestBody SubscribeVO subscribeVO) {
        userInfoService.updateUserSubscribe(subscribeVO);
        return ResultVO.ok();
        
    }
    @OptLog(optType = UPDATE)
    @Operation(summary = "修改用户角色")
    @PutMapping("/admin/users/role")
    public ResultVO<?> updateUserRole(@Valid @RequestBody UserRoleVO userRoleVO) {
        userInfoService.updateUserRole(userRoleVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改用户禁用状态")
    @PutMapping("/admin/users/disable")
    public ResultVO<?> updateUserDisable(@Valid @RequestBody UserDisableVO userDisableVO) {
        userInfoService.updateUserDisable(userDisableVO);
        return ResultVO.ok();
    }

    @Operation(summary = "查看在线用户")
    @GetMapping("admin/users/online")
    public ResultVO<PageResultDTO<UserOnlineDTO>> listOnlineUsers(ConditionVO conditionVO) {
        return ResultVO.ok(userInfoService.listOnlineUsers(conditionVO));
    }
    @OptLog(optType = DELETE)
    @Operation(summary = "下线用户")
    @DeleteMapping("/admin/users/{userInfoId}/online")
    public ResultVO<?> removeOnlineUser(@PathVariable("userInfoId") Integer userInfoId) {
        userInfoService.removeOnlineUser(userInfoId);
        return ResultVO.ok();
    }

    @Operation(summary = "根据id获取用户信息")
    @GetMapping("/users/info/{userInfoId}")
    public ResultVO<UserInfoDTO> getUserInfoById(@PathVariable("userInfoId") Integer userInfoId) {
        return ResultVO.ok(userInfoService.getUserInfoById(userInfoId));
    }

}
