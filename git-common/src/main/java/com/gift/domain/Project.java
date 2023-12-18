package com.gift.domain;

import lombok.Data;

import javax.swing.text.Position;
import java.util.List;

/**
 * 项目实体类
 *
 * 需注意在 MySql中数据库表中要下划线
 */
@Data
public class Project {
    private String itemName;
    private Integer stars;
    private String description;
    private String loginName;
    private String repository;
    private Integer size;
    private Integer id;
    private String fullName;
    private String defaultBranch;
}
