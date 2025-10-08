package com.example.framework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.framework.entity.UserAuth;
import com.example.framework.model.dto.UserAdminDTO;
import com.example.framework.model.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserAuthMapper  extends BaseMapper<UserAuth>{

    Integer countUser(@Param("conditionVO") ConditionVO conditionVO);

    List<UserAdminDTO> listUsers(@Param("current") Long current,
                                 @Param("size") Long size,@Param("conditionVO") ConditionVO conditionVO);
}
