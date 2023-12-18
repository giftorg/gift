package com.gift.service;

import com.gift.domain.Repository;

import java.util.List;

/**
 * 仓库接口
 */
public interface RepositoryService {

    void testIndex(String index);

    List<Repository> SearchRepository(String text);
}
