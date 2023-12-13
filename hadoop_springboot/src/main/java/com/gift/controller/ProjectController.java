package com.gift.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gift.domain.Project;
import com.gift.domain.Repository;
import com.gift.domain.Result;
import com.gift.domain.Vo.FunctionVo;
import com.gift.service.ChatService;
import com.gift.service.ProjectService;
import com.gift.service.RepositoryService;
import com.gift.service.impl.RepositoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private RepositoryService repositoryService;

    //测试
    @GetMapping("/{id}")
    public Result test1(@PathVariable Integer id){
        System.out.println("Gift quickstart02 ...");
        return new Result(true,projectService.testGetById(id));
    }

    //TODO 根据关键字(语言？)来推荐项目列表 返回一个项目列表对象 Project
    @GetMapping("/recommend/{keys}")
    public Result recommend(@PathVariable String keys){
        //切分
        //搜索
        //返回
        return new Result();
    }

    //TODO 根据字符串(name？repoId？)来搜索项目列表，返回一个仓库列表 Repository
    @GetMapping("/project")
    public Result searchProject(@RequestParam String text){
        Boolean flag = true;
        log.info("你输入的仓库搜索词为：",text);
        List<Repository> repositories = repositoryService.SearchRepository(text);
        int size = repositories.size();

        if (repositories == null || size == 0){
            flag = false;
        }

        return new Result(flag,repositories);
    }

    //TODO 根据搜索词项目列表 返回代码列表对象 FuntionVo
    @GetMapping("/code")
    public Result searchCode(@RequestParam String keys){
        Boolean flag = true;

        //返回一个Vo
        List<FunctionVo> functionVos = projectService.getByCode(keys);


        if (functionVos == null || functionVos.size() == 0){ //若为空
            flag = false;
        }
        return new Result(flag,functionVos);
    }

    //TODO 分页功能
    //分页
    @GetMapping("/{currentPage}/{pageSize}")
    public Result getPage(@PathVariable int currentPage, @PathVariable int pageSize, Project project){
        //System.out.println("参数=======>" + project);
        IPage<Project> page = new Page(currentPage,pageSize);      //创建一个Page对象,参数是目前的页码，和一页的数据量

        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<Project>();  //查询对象
        lqw.like(Strings.isNotEmpty(project.getItemName()),Project::getItemName,project.getItemName());  //匹配查询的字符串
        lqw.like(Strings.isNotEmpty(project.getLoginName()),Project::getLoginName,project.getLoginName());
        lqw.like(Strings.isNotEmpty(project.getDescription()),Project::getDescription,project.getDescription());

        IPage<Project> page1 = projectService.page(page,lqw);    //调用Mybatisplus的接口方法，参数是页码对象，和查询的语句


        //如果当前页码值大于了总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
        if (currentPage > (int)page1.getPages()){
            return new Result(true,projectService.page(new Page(1,pageSize),lqw)); //回到初始页面
        }
        return new Result(page != null,page1);
    }

//    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public

    //TODO 调用大模型接口
    @PostMapping("/chat")
    public String Chat(@RequestParam String question){
        log.info("你当前查询的问题是{}",question);
        String answer = chatService.chat(question);

//        chatService.chat(question).forEach(System.out::println);
        return answer;
    }


    /**
     * 添加ES索引
     * @param index
     * @return
     */
    @GetMapping("/test")
    public Result testIndex(@RequestParam String index){
        repositoryService.testIndex(index);
        return new Result(true,"成功建立索引");
    }


}
