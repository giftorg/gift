package org.giftorg.backed.service;

import org.giftorg.backed.entity.vo.FunctionVo;

import java.util.List;

public interface CodeService {

    /**
     * 根据代码搜索词来向ES查询
     */
    List<FunctionVo> searchCode(String query);
}
