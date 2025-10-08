package com.example.framework.controller;


import com.example.framework.annotation.OptLog;
import com.example.framework.model.dto.LabelOptionDTO;
import com.example.framework.model.dto.ResourceDTO;
import com.example.framework.model.vo.ConditionVO;
import com.example.framework.model.vo.ResourceVO;
import com.example.framework.service.ResourceService;
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
@Tag(name = "资源模块")
@RestController
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Operation(summary = "查看资源列表")
    @GetMapping("/admin/resources")
    public ResultVO<List<ResourceDTO>> listResources(ConditionVO conditionVO) {
        return ResultVO.ok(resourceService.listResources(conditionVO));
    }
    @OptLog(optType = DELETE)
    @Operation(summary = "删除资源")
    @DeleteMapping("/admin/resources/{resourceId}")
    public ResultVO<?> deleteResource(@PathVariable("resourceId") Integer resourceId) {
        resourceService.deleteResource(resourceId);
        return ResultVO.ok();
    }

    @OptLog(optType = SAVE_OR_UPDATE)
    @Operation(summary = "新增或修改资源")
    @PostMapping("/admin/resources")
    public ResultVO<?> saveOrUpdateResource(@RequestBody @Valid ResourceVO resourceVO) {
        resourceService.saveOrUpdateResource(resourceVO);
        return ResultVO.ok();
    }

    @Operation(summary = "查看角色资源选项")
    @GetMapping("/admin/role/resources")
    public ResultVO<List<LabelOptionDTO>> listResourceOption() {
        return ResultVO.ok(resourceService.listResourceOption());
    }

}

