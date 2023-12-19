package org.giftorg.backed.entity.repository;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "projects")
public class Project {
    private Integer id;

    private Integer repoId;

    private String name;

    private String fullName;

    private Integer stars;

    private String author;

    private String url;

    private String description;

    private Integer size;

    private String defaultBranch;

    private String readme;

    private String readmeCn;

    private List<String> tags;
}
