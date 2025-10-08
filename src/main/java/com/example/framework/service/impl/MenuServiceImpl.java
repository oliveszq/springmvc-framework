package com.example.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.framework.entity.Menu;
import com.example.framework.entity.RoleMenu;
import com.example.framework.exception.BizException;
import com.example.framework.mapper.MenuMapper;
import com.example.framework.mapper.RoleMenuMapper;
import com.example.framework.model.dto.LabelOptionDTO;
import com.example.framework.model.dto.MenuDTO;
import com.example.framework.model.dto.UserMenuDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.IsHiddenVO;
import com.example.framework.model.vo.MenuVO;
import com.example.framework.service.MenuService;
import com.example.framework.utils.BeanCopyUtil;
import com.example.framework.utils.UserUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.framework.constant.CommonConstant.COMPONENT;
import static com.example.framework.constant.CommonConstant.TRUE;

/**
 * @author: qinglong
 * @create-date: 2023/12/15 16:18
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<MenuDTO> listMenus(ConditionVO conditionVO) {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .like(StringUtil.isNotBlank(conditionVO.getKeywords()), Menu::getName, conditionVO.getKeywords()));
        List<Menu> catalogs = listCatalogs(menus);
        Map<Integer,List<Menu>> childrenMap= getMenuMap(menus);
        List<MenuDTO> menuDTOs = catalogs.stream().map(item -> {
            MenuDTO menuDTO = BeanCopyUtil.copyObject(item, MenuDTO.class);
            List<MenuDTO> list = BeanCopyUtil.copyList(childrenMap.get(item.getId()), MenuDTO.class).stream()
                    .sorted(Comparator.comparing(MenuDTO::getOrderNum))
                    .collect(Collectors.toList());
            menuDTO.setChildren(list);
            childrenMap.remove(item.getId());
            return menuDTO;
        }).sorted(Comparator.comparing(MenuDTO::getOrderNum)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(childrenMap)) {
            List<Menu> childrenList = new ArrayList<>();
            childrenMap.values().forEach(childrenList::addAll);
            List<MenuDTO> childrenDTOList = childrenList.stream()
                    .map(item -> BeanCopyUtil.copyObject(item, MenuDTO.class))
                    .sorted(Comparator.comparing(MenuDTO::getOrderNum))
                    .collect(Collectors.toList());
            menuDTOs.addAll(childrenDTOList);
        }
        return menuDTOs;
    }

    @Override
    public List<UserMenuDTO> listUserMenus() {
        List<Menu> menus = menuMapper.listMenusByUserInfoId(UserUtil.getUserDetailsDTO().getUserInfoId());
        List<Menu> catalogs = listCatalogs(menus);
        Map<Integer, List<Menu>> childrenMap = getMenuMap(menus);

        return convertUserMenuList(catalogs,childrenMap);
    }

    /**
     *对菜单按照父id进行分组
     * @param menus
     * @return
     */
    private Map<Integer, List<Menu>> getMenuMap(List<Menu> menus){
        Map<Integer, List<Menu>> collect = menus.stream()
                .filter(item -> Objects.nonNull(item.getParentId()))
                .collect(Collectors.groupingBy(Menu::getParentId));
        return collect;
    }

    /**
     * 从菜单表中筛选出目录
     * @param menus
     * @return
     */
    private List<Menu> listCatalogs(List<Menu> menus) {
        return menus.stream().filter(item-> Objects.isNull(item.getParentId()))
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .collect(Collectors.toList());
    }

    private List<UserMenuDTO> convertUserMenuList(List<Menu> catalogs , Map<Integer,List<Menu>> childrenMap){
        return catalogs.stream().map(item->{
            UserMenuDTO userMenuDTO = new UserMenuDTO();
            List<UserMenuDTO> list = new LinkedList<>();
            List<Menu> children = childrenMap.get(item.getId());
            if (CollectionUtils.isNotEmpty(children)){
                userMenuDTO = BeanCopyUtil.copyObject(item, UserMenuDTO.class);
                list = children.stream().sorted(Comparator.comparing(Menu::getOrderNum))
                        .map(menu -> {
                            UserMenuDTO dto = BeanCopyUtil.copyObject(menu, UserMenuDTO.class);
                            dto.setHidden(menu.getIsHidden().equals(TRUE));
                            return dto;
                        }).collect(Collectors.toList());
            }else {
                userMenuDTO.setPath(item.getPath());
                userMenuDTO.setComponent(COMPONENT);
                list.add(UserMenuDTO.builder()
                                .path("")
                                .name(item.getName())
                                .icon(item.getIcon())
                                .component(item.getComponent())
                        .build());
            }
            userMenuDTO.setHidden(item.getIsHidden().equals(TRUE));
            userMenuDTO.setChildren(list);
            return userMenuDTO;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateMenu(MenuVO menuVO) {
        Menu menu = BeanCopyUtil.copyObject(menuVO, Menu.class);
        this.saveOrUpdate(menu);
    }

    @Override
    public void updateMenuIsHidden(IsHiddenVO isHiddenVO) {
        Menu menu = BeanCopyUtil.copyObject(isHiddenVO, Menu.class);
        menuMapper.updateById(menu);
    }

    @Override
    public void deleteMenu(Integer menuId) {
        Long count = roleMenuMapper.selectCount(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getMenuId, menuId));
        if (count > 0) {
            throw new BizException("菜单下有角色关联");
        }
        List<Integer> menuIds = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                        .select(Menu::getId)
                        .eq(Menu::getParentId, menuId))
                .stream()
                .map(Menu::getId)
                .collect(Collectors.toList());
        menuIds.add(menuId);
        menuMapper.deleteBatchIds(menuIds);
    }

    @Override
    public List<LabelOptionDTO> listMenuOptions() {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .select(Menu::getId, Menu::getName, Menu::getParentId, Menu::getOrderNum));
        List<Menu> catalogs = listCatalogs(menus);
        Map<Integer, List<Menu>> childrenMap = getMenuMap(menus);
        return catalogs.stream().map(catalog -> {
            List<LabelOptionDTO> list = new ArrayList<>();
            List<Menu> menuList = childrenMap.get(catalog.getId());
            if(CollectionUtils.isNotEmpty(menuList)){
                list = menuList.stream()
                        .sorted(Comparator.comparing(Menu::getOrderNum))
                        .map(item -> LabelOptionDTO.builder()
                                .id(item.getId())
                                .label(item.getName())
                                .build()
                        ).collect(Collectors.toList());
            }
            return LabelOptionDTO.builder()
                    .id(catalog.getId())
                    .label(catalog.getName())
                    .children(list)
                    .build();
        }).collect(Collectors.toList());
    }

    private Map<Integer, List<Menu>> getMenuChildren(List<Integer> menuIds){
        List<Menu> resourceList = menuMapper.selectBatchIds(menuIds);
        return resourceList.stream().filter(item ->
                        Objects.nonNull(item.getParentId()))
                .collect(Collectors.groupingBy(Menu::getParentId));
    }
}
