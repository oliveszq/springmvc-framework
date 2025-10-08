package com.example.framework.controller;

import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.LabelOptionDTO;
import com.example.framework.model.dto.MenuDTO;
import com.example.framework.model.dto.UserMenuDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.IsHiddenVO;
import com.example.framework.model.vo.MenuVO;
import com.example.framework.service.MenuService;
import com.example.framework.utils.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.framework.constant.OptTypeConstant.*;

/**
 * @author: qinglong
 * @create-date: 2023/12/15 16:16
 */
@Tag(name = "菜单模块")
@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Operation(summary = "查看菜单列表")
    @GetMapping("/admin/menus")
    public ResultVO<List<MenuDTO>> listMenus(ConditionVO conditionVO){
        return ResultVO.ok(menuService.listMenus(conditionVO));
    }
    @OptLog(optType =SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改菜单")
    @PostMapping("/admin/menus")
    public ResultVO<?> saveOrUpdateMenu(@Valid @RequestBody MenuVO menuVO) {
        menuService.saveOrUpdateMenu(menuVO);
        return ResultVO.ok();
    }
    @OptLog(optType = UPDATE)
    @Operation(summary = "修改目录是否隐藏")
    @PutMapping("/admin/menus/isHidden")
    public ResultVO<?> updateMenuIsHidden(@RequestBody IsHiddenVO isHiddenVO) {
        menuService.updateMenuIsHidden(isHiddenVO);
        return ResultVO.ok();
    }

    @OptLog(optType = DELETE)
    @Operation(summary = "删除菜单")
    @DeleteMapping("/admin/menus/{menuId}")
    public ResultVO<?> deleteMenu(@PathVariable("menuId") Integer menuId) {
        menuService.deleteMenu(menuId);
        return ResultVO.ok();
    }

    @Operation(summary = "查看角色菜单选项")
    @GetMapping("/admin/role/menus")
    public ResultVO<List<LabelOptionDTO>> listMenuOptions() {
        return ResultVO.ok(menuService.listMenuOptions());
    }

    @Operation(summary = "查看用户菜单列表")
    @GetMapping("/admin/user/menus")
    public ResultVO<List<UserMenuDTO>> listUserMenus(){
        return ResultVO.ok(menuService.listUserMenus());
    }

}
