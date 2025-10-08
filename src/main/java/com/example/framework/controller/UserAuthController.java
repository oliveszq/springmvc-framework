package com.example.framework.controller;

import com.example.framework.annotation.AccessLimit;
import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.*;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.PasswordVO;
import com.example.framework.model.vo.QQLoginVO;
import com.example.framework.model.vo.UserVO;
import com.example.framework.service.UserAuthService;
import com.example.framework.utils.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.framework.constant.OptTypeConstant.UPDATE;

/**
 * @Author: QLong
 * @Date: 2023/3/15
 **/
@Tag(name = "用户账号模块")
@RestController
@Schema(description = "用户账号")
public class UserAuthController {
    @Resource
    private UserAuthService userAuthService;

    @AccessLimit(seconds = 60,maxCount = 1)
    @Operation(summary = "发送邮箱验证码")
    @Parameter(name = "username", description = "用户名", required = true, schema = @Schema(type = "string"))
    @GetMapping("/users/code")
    public ResultVO<?> sendCode(String username){
        userAuthService.sendCode(username);
        return ResultVO.ok();
    }
    @Operation(summary = "获取用户区域分布")
    @GetMapping("/admin/users/area")
    public ResultVO<List<UserAreaDTO>> listUserAreas(ConditionVO conditionVO){
        return ResultVO.ok(userAuthService.listUserAreas(conditionVO));
    }

    @Operation(summary = "查询后台用户列表")
    @GetMapping("/admin/users")
    public ResultVO<PageResultDTO<UserAdminDTO>> listUsers(ConditionVO conditionVO){
        return ResultVO.ok(userAuthService.listUsers(conditionVO));
    }

    @Operation(summary = "注册")
    @PostMapping("/users/register")
    public ResultVO<?> register(@Valid @RequestBody UserVO userVO){
        userAuthService.register(userVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改密码")
    @PutMapping("/users/password")
    public ResultVO<?> updatePassword(@Valid @RequestBody UserVO user) {
        userAuthService.updatePassword(user);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public ResultVO<?> updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return ResultVO.ok();
    }

    @Operation(summary = "用户登出")
    @PostMapping("/users/logout")
    public ResultVO<UserLogoutStatusDTO> logout(){
        return ResultVO.ok(userAuthService.logout());
    }

    @Operation(summary = "qq登录")
    @PostMapping("/users/oauth/qq")
    public ResultVO<UserInfoDTO> qqLogin(@Valid @RequestBody QQLoginVO qqLoginVO) {
        return ResultVO.ok(userAuthService.qqLogin(qqLoginVO));
    }
}
