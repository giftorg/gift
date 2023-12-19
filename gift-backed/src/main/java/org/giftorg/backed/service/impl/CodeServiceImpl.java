package org.giftorg.backed.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.backed.dao.FunctionESDao;
import org.giftorg.backed.entity.code.Function;
import org.giftorg.backed.entity.vo.FunctionVo;
import org.giftorg.backed.mapper.ProjectMapper;
import org.giftorg.backed.service.CodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {

    @Resource
    private ProjectMapper projectMapper;

    private static final FunctionESDao fd = new FunctionESDao();

    /**
     * 向ES查询并返回结果代码对象
     */
    @Override
    public List<FunctionVo> searchCode(String keys) {
        try {
            List<Function> functions = fd.retrieval(keys);
            ArrayList<FunctionVo> functionVos = new ArrayList<>();
            for (Function function : functions) {
                FunctionVo functionVo = new FunctionVo();
                BeanUtils.copyProperties(function, functionVo);
                functionVos.add(functionVo);
            }
            return functionVos;

        } catch (Exception e) {
            log.error("search elasticsearch code failed: {}", e.getMessage(), e);
        }

        return null;
    }
}
