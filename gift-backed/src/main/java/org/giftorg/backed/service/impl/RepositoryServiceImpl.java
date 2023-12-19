package org.giftorg.backed.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.giftorg.backed.entity.repository.Project;
import org.giftorg.backed.entity.repository.Repository;
import org.giftorg.backed.dao.RepositoryESDao;
import org.giftorg.backed.mapper.ProjectMapper;
import org.giftorg.backed.service.RepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl  extends ServiceImpl<ProjectMapper, Project> implements RepositoryService {

    @Resource
    private ProjectMapper projectMapper;

    private static final RepositoryESDao rd = new RepositoryESDao();

    @Override
    public List<Repository> searchRepository(String query) throws Exception {
        return rd.retrieval(query);
    }
}
