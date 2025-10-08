package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.Menu;
import com.example.framework.model.dto.LabelOptionDTO;
import com.example.framework.model.dto.MenuDTO;
import com.example.framework.model.dto.UserMenuDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.IsHiddenVO;
import com.example.framework.model.vo.MenuVO;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<MenuDTO> listMenus(ConditionVO conditionVO);

    void saveOrUpdateMenu(MenuVO menuVO);

    void updateMenuIsHidden(IsHiddenVO isHiddenVO);

    void deleteMenu(Integer menuId);

    List<LabelOptionDTO> listMenuOptions();

    List<UserMenuDTO> listUserMenus();

}
