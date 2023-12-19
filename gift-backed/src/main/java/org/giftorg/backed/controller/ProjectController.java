package org.giftorg.backed.controller;

import lombok.extern.slf4j.Slf4j;
import org.giftorg.backed.entity.Response;
import org.giftorg.backed.entity.repository.Project;
import org.giftorg.backed.entity.repository.Repository;
import org.giftorg.backed.entity.vo.FunctionVo;
import org.giftorg.backed.service.ChatService;
import org.giftorg.backed.service.CodeService;
import org.giftorg.backed.service.RepositoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProjectController {

    @Resource
    private CodeService codeService;

    @Resource
    private ChatService chatService;

    @Resource
    private RepositoryService repositoryService;

    @GetMapping("/recommend")
    public Response recommend() {
        // TODO 根据关键字(语言？)来推荐项目列表 返回一个项目列表对象 Project
        return new Response();
    }

    @GetMapping("/search/project")
    public Response searchProject(@RequestParam String query) {
        log.info("search project query: {}", query);
        List<Repository> repositories = null;
        try {
            repositories = repositoryService.searchRepository(query);
        } catch (Exception e) {
            log.error("search project failed: {}", e.getMessage(), e);
            return new Response(1, "search project failed");
        }
        return new Response(repositories);
    }

    @GetMapping("/search/code")
    public Response searchCode(@RequestParam String query) {
        List<FunctionVo> functionVos = codeService.searchCode(query);
        for (FunctionVo func : functionVos) {
            Project repo = repositoryService.getById(func.getRepoId());
            func.setProject(repo);
        }
        return new Response(functionVos);
    }

    @PostMapping("/chat")
    public Response Chat(@RequestParam String question) {
        log.info("chat request question: {}", question);
        String answer = null;
        try {
            answer = chatService.chat(question);
        } catch (Exception e) {
            log.error("chat request failed: {}", e.getMessage(), e);
            return new Response(1, "chat request failed");
        }
        return new Response(answer);
    }
}
