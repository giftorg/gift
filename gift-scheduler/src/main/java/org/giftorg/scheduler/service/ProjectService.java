package org.giftorg.scheduler.service;

import org.giftorg.scheduler.entity.Project;

import java.util.List;

public interface ProjectService {

    Boolean cloneAndPutProject(String remote);

    List<Project> getProjectList();
}
