package com.gift.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gift.domain.Project;
import com.gift.mapper.ProjectMapper;
import com.gift.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public void chat() {

    }

    @Override
    public Project testGetById(Integer id) {

        return projectMapper.selectById(id);
    }


}
