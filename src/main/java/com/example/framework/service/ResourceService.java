package com.example.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.framework.entity.Resource;
import com.example.framework.model.dto.LabelOptionDTO;
import com.example.framework.model.dto.ResourceDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.ResourceVO;

import java.util.List;

/**
 * @author qinglong
 * @since 2024-08-26
 */
public interface ResourceService extends IService<Resource> {

    void importSwagger();
    List<ResourceDTO> listResources(ConditionVO conditionVO);

    void deleteResource(Integer resourceId);

    void saveOrUpdateResource(ResourceVO resourceVO);

    List<LabelOptionDTO> listResourceOption();



}
