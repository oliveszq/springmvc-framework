package com.example.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.framework.entity.Resource;
import com.example.framework.entity.RoleResource;
import com.example.framework.exception.BizException;
import com.example.framework.handler.CustomAuthorizationManagerImpl;
import com.example.framework.mapper.ResourceMapper;
import com.example.framework.mapper.RoleResourceMapper;
import com.example.framework.model.dto.LabelOptionDTO;
import com.example.framework.model.dto.ResourceDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.ResourceVO;
import com.example.framework.service.ResourceService;
import com.example.framework.utils.BeanCopyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.framework.constant.CommonConstant.FALSE;

/**
 * @author qinglong
 * @since 2024-08-26
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private RoleResourceMapper roleResourceMapper;
    @Autowired
    private CustomAuthorizationManagerImpl customAuthorizationManager;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 导入Swagger文档到系统资源中
     * 此方法主要用于从Swagger生成的API文档中提取信息，如API的标签和路径，
     * 并根据这些信息在当前系统中创建相应的资源节点
     * 使用了@Transactional注解，确保操作的原子性，遇到异常会回滚
     */
    @SuppressWarnings("all")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importSwagger() {
        // 清除现有资源和角色资源关系，准备导入新的Swagger数据
        this.remove(null);
        roleResourceMapper.delete(null);

        // 初始化资源列表，用于存储从Swagger提取的资源信息
        List<Resource> resources = new ArrayList<>();

        // 使用RestTemplate获取Swagger的API文档数据
        Map<String, Object> data = restTemplate.getForObject("http://localhost:8080/v2/api-docs", Map.class);

        // 提取并处理文档中的标签信息，转化为资源对象
        List<Map<String, String>> tagList = (List<Map<String, String>>) data.get("tags");
        tagList.forEach(item -> {
            Resource resource = Resource.builder()
                    .resourceName(item.get("name"))
                    .isAnonymous(FALSE)
                    .createTime(LocalDateTime.now())
                    .build();
            resources.add(resource);
        });

        // 批量保存处理后的资源对象到数据库
        this.saveBatch(resources);

        // 构建资源名称与ID的映射，以便后续查找父资源ID
        Map<String, Integer> permissionMap = resources.stream()
                .collect(Collectors.toMap(Resource::getResourceName, Resource::getId));

        // 清空资源列表，准备处理API路径信息
        resources.clear();

        // 提取并处理文档中的路径信息，构建API资源对象
        Map<String, Map<String, Map<String, Object>>> path =
                (Map<String, Map<String, Map<String, Object>>>) data.get("paths");
        path.forEach((url, value) -> value.forEach((requestMethod, info) -> {
            String permissionName = info.get("summary").toString();
            List<String> tag = (List<String>) info.get("tags");
            Integer parentId = permissionMap.get(tag.get(0));

            Resource resource = Resource.builder()
                    .resourceName(permissionName)
                    .url(url.replaceAll("\\{[^}]*\\}", "*"))
                    .parentId(parentId)
                    .requestMethod(requestMethod.toUpperCase())
                    .isAnonymous(FALSE)
                    .createTime(LocalDateTime.now())
                    .build();

            resources.add(resource);
        }));

        // 批量保存处理后的API资源对象到数据库
        this.saveBatch(resources);
    }

    @Override
    public List<ResourceDTO> listResources(ConditionVO conditionVO) {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),
                        Resource::getResourceName, conditionVO.getKeywords()));

        List<Resource> parents = listResourceModule(resources);
        Map<Integer, List<Resource>> childrenMap = listResourceChildren(resources);
        List<ResourceDTO> resourceDTOs = parents.stream().map(item -> {
            ResourceDTO resourceDTO = BeanCopyUtil.copyObject(item, ResourceDTO.class);
            List<ResourceDTO> child = BeanCopyUtil.copyList(childrenMap.get(item.getId()), ResourceDTO.class);
            resourceDTO.setChildren(child);
            childrenMap.remove(item.getId());
            return resourceDTO;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(childrenMap)) {
            List<Resource> childrenList = new ArrayList<>();
            childrenMap.values().forEach(childrenList::addAll);
            List<ResourceDTO> childrenDTOs = childrenList.stream()
                    .map(item -> BeanCopyUtil.copyObject(item, ResourceDTO.class))
                    .collect(Collectors.toList());
            resourceDTOs.addAll(childrenDTOs);
        }
        return resourceDTOs;
    }

    @Override
    public void deleteResource(Integer resourceId) {
        Long count = roleResourceMapper.selectCount(new LambdaQueryWrapper<RoleResource>()
                .eq(RoleResource::getResourceId, resourceId));
        if (count > 0) {
            throw new BizException("该资源下存在角色");
        }
        List<Integer> resourceIds = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                        .select(Resource::getId).
                        eq(Resource::getParentId, resourceId))
                .stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
        resourceIds.add(resourceId);
        resourceMapper.deleteBatchIds(resourceIds);
    }

    @Override
    public void saveOrUpdateResource(ResourceVO resourceVO) {
        Resource resource = BeanCopyUtil.copyObject(resourceVO, Resource.class);
        this.saveOrUpdate(resource);
        customAuthorizationManager.clearDataSource();
    }

    @Override
    public List<LabelOptionDTO> listResourceOption() {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .select(Resource::getId, Resource::getResourceName, Resource::getParentId)
                .eq(Resource::getIsAnonymous, FALSE));

        List<Resource> parents = listResourceModule(resources);
        Map<Integer, List<Resource>> childrenMap = listResourceChildren(resources);
        return parents.stream().map(item -> {
            List<LabelOptionDTO> list = new ArrayList<>();
            List<Resource> children = childrenMap.get(item.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                list = children.stream()
                        .map(resource -> LabelOptionDTO.builder()
                                .id(resource.getId())
                                .label(resource.getResourceName())
                                .build())
                        .collect(Collectors.toList());
            }
            return LabelOptionDTO.builder()
                    .id(item.getId())
                    .label(item.getResourceName())
                    .children(list)
                    .build();
        }).collect(Collectors.toList());
    }

    private List<Resource> listResourceModule(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(item -> Objects.isNull(item.getParentId()))
                .collect(Collectors.toList());
    }

    private Map<Integer, List<Resource>> listResourceChildren(List<Resource> resourceList) {
        return resourceList.stream()
                .filter(item -> Objects.nonNull(item.getParentId()))
                .collect(Collectors.groupingBy(Resource::getParentId));
    }
}
