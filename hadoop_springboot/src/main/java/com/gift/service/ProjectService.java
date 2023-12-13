package com.gift.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gift.domain.Project;
import com.gift.domain.Vo.FunctionVo;

import java.util.List;

public interface ProjectService extends IService<Project> {
    //TODO 推荐

    //搜索关键字

    //搜素代码


    Project testGetById(Integer id);

    /**
     * 根据代码搜索词来向ES查询
     * @param keys
     * @return
     */
    List<FunctionVo> getByCode(String keys);
}
