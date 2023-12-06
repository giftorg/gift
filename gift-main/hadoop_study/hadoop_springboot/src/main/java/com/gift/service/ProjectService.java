package com.gift.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gift.domain.Project;

public interface ProjectService extends IService<Project> {
    //TODO 推荐

    //搜索关键字

    //搜素代码

    //GPT接口
    void chat();


    Project testGetById(Integer id);
}
