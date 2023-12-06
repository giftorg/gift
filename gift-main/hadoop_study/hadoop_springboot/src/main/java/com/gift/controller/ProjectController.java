package com.gift.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gift.domain.Project;
import com.gift.domain.R;
import com.gift.service.ProjectService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apis")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    //测试
    @GetMapping("/{id}")
    public R test1(@PathVariable Integer id){
        System.out.println("Gift quickstart02 ...");
        return new R(true,projectService.testGetById(id));
    }

    //TODO 根据关键字来推荐项目列表 返回一个项目列表对象 Project
    @GetMapping("/recommend/{keys}")
    public R recommend(@PathVariable String keys){
        //切分
        //搜索
        //返回
        return new R();
    }

    //TODO 根据字符串来搜索项目列表，返回一个项目列表 Project
    @GetMapping("/project/{keys}")
    public R searchProject(@PathVariable String keys){
        return new R();
    }

    //TODO 根据搜索词项目列表 返回代码列表对象 Funtion
    @GetMapping("/code/{keys}")
    public R searchCode(@PathVariable String keys){

        return new R();
    }

    //TODO 分页功能
    //分页
    @GetMapping("/{currentPage}/{pageSize}")
    public R getPage(@PathVariable int currentPage, @PathVariable int pageSize, Project project){
        //System.out.println("参数=======>" + project);
        IPage<Project> page = new Page(currentPage,pageSize);      //创建一个Page对象,参数是目前的页码，和一页的数据量

        LambdaQueryWrapper<Project> lqw = new LambdaQueryWrapper<Project>();  //查询对象
        lqw.like(Strings.isNotEmpty(project.getItemName()),Project::getItemName,project.getItemName());  //匹配查询的字符串
        lqw.like(Strings.isNotEmpty(project.getLoginName()),Project::getLoginName,project.getLoginName());
        lqw.like(Strings.isNotEmpty(project.getDescription()),Project::getDescription,project.getDescription());

        IPage<Project> page1 = projectService.page(page,lqw);    //调用Mybatisplus的接口方法，参数是页码对象，和查询的语句


        //如果当前页码值大于了总页码值，那么重新执行查询操作，使用最大页码值作为当前页码值
        if (currentPage > (int)page1.getPages()){
            return new R(true,projectService.page(new Page(1,pageSize),lqw)); //回到初始页面
        }
        return new R(page != null,page1);
    }

    //TODO 调用大模型接口
    @PostMapping("/chat")
    public void Chat(){
        projectService.chat();
        return;
    }


}
