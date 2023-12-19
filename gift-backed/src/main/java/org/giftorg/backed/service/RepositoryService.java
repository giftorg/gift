package org.giftorg.backed.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.giftorg.backed.entity.repository.Project;
import org.giftorg.backed.entity.repository.Repository;

import java.util.List;

/**
 * 仓库接口
 */
public interface RepositoryService  extends IService<Project> {

    List<Repository> searchRepository(String text) throws Exception;
}
