package org.giftorg.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.giftorg.backed.entity.repository.Project;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

}
