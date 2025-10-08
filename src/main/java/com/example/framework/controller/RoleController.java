package com.example.framework.controller;


import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.PageResultDTO;
import com.example.framework.model.dto.RoleDTO;
import com.example.framework.model.dto.UserRoleDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.RoleVO;
import com.example.framework.service.RoleService;
import com.example.framework.utils.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.framework.constant.OptTypeConstant.DELETE;
import static com.example.framework.constant.OptTypeConstant.SAVE_OR_UPDATE;

/**
 * @author qinglong
 * @since 2024-08-26
 */
@Tag(name = "角色模块")
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "查询用户角色选项")
    @GetMapping("admin/users/role")
    public ResultVO<List<UserRoleDTO>> listUserRoles(){
        return ResultVO.ok(roleService.listUserRoles());
    }

    @Operation(summary = "查询角色列表")
    @GetMapping("/admin/roles")
    public ResultVO<PageResultDTO<RoleDTO>> listRoles(ConditionVO conditionVO) {
        return ResultVO.ok(roleService.listRoles(conditionVO));
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新角色")
    @PostMapping("/admin/role")
    public ResultVO<?> saveOrUpdateRole(@RequestBody @Valid RoleVO roleVO) {
        roleService.saveOrUpdateRole(roleVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除角色")
    @DeleteMapping("/admin/roles")
    public ResultVO<?> deleteRoles(@RequestBody List<Integer> roleIdList) {
        roleService.deleteRoles(roleIdList);
        return ResultVO.ok();
    }

}

