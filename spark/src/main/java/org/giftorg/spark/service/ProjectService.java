package org.giftorg.spark.service;

import org.giftorg.spark.entity.Project;

import java.io.IOException;
import java.util.List;

public interface ProjectService {

    Boolean cloneAndPutProject(String remote);

    List<Project> getProjectList();
}
