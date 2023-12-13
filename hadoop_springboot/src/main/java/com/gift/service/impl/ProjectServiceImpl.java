package com.gift.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gift.domain.Project;
import com.gift.domain.Vo.FunctionVo;
import com.gift.mapper.ProjectMapper;
import com.gift.service.ProjectService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.giftorg.analyze.dao.FunctionESDao;
import org.giftorg.analyze.entity.Function;
import org.giftorg.common.elasticsearch.Elasticsearch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;


    @Override
    public Project testGetById(Integer id) {

        return projectMapper.selectById(id);
    }

    /**
     * 向ES查询并返回结果代码对象
     * @param keys
     * @return
     */
    @Override
    public List<FunctionVo> getByCode(String keys) {

        FunctionESDao functionESDao = new FunctionESDao();
        try {
            List<Function> functions = functionESDao.retrieval(keys);

            //转换为Vo
            FunctionVo functionVo = new FunctionVo();
            ArrayList<FunctionVo> functionVos = new ArrayList<>();

            functions.forEach(function -> {
                BeanUtils.copyProperties(function,functionVo);
                functionVos.add(functionVo);
            });

            functionVos.forEach(System.out::println);

            Elasticsearch.close();

            return functionVos;

        } catch (Exception exception) {
            log.info("查询出错,{}",exception);
        }

        return null;
    }


}
