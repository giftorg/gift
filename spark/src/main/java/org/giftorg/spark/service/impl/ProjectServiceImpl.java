package org.giftorg.spark.service.impl;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.giftorg.spark.dao.HDFSDao;
import org.giftorg.spark.entity.Project;
import org.giftorg.spark.mapper.ProjectMapper;
import org.giftorg.spark.service.ProjectService;
import org.giftorg.spark.utils.GitUtil;
import org.giftorg.spark.utils.PathUtil;

import java.io.InputStream;
import java.util.List;

@Slf4j
public class ProjectServiceImpl implements ProjectService {
    public Boolean putToHDFS(String local, String hdfsUrl) {
        try {
            HDFSDao.put(local, hdfsUrl);
        } catch (Exception e) {
            log.error("put repository {} to HDFS error: {}", local, e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Boolean cloneAndPutProject(String remote) {
        String local = FileUtil.getAbsolutePath(PathUtil.join("repositories", PathUtil.base(remote)));

        // 将项目先拉取到本地
        log.info("git cloning {}", remote);
        if (!GitUtil.gitClone(remote, local)) {
            log.error("git clone failed. remote: {}, local: {}", remote, local);
            return false;
        }
        log.info("git clone success. remote: {}, local: {}", remote, local);

        // 将项目上传到HDFS
        String hdfsUrl = HDFSDao.hdfsRepoPath(remote.replace("https://github.com", ""));
        log.info("putting repository {} to HDFS {}", local, hdfsUrl);
        if (putToHDFS(local, hdfsUrl)) {
            log.info("put repository {} to HDFS success", local);
        }

        // 删除本地项目
        try {
            FileUtil.del(local);
        } catch (Exception e) {
            log.error("delete local repository {} error: {}", local, e.getMessage());
        }

        return true;
    }


    @Override
    public List<Project> getProjectList() {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession();

            ProjectMapper mapper = sqlSession.getMapper(ProjectMapper.class);
            List<Project> projectList = mapper.selectList();

            sqlSession.close();
            return projectList;
        } catch (Exception e) {
            log.error("get project list error: {}", e.getMessage());
            return null;
        }
    }
}
