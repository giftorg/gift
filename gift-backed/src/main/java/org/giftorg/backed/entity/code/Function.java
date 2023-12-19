package org.giftorg.backed.entity.code;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 在ES中搜索到的返回代码结果实体类
 */
@Data
public class Function implements Serializable {
    private String name;    //项目名

    private String source;  //源代码

    private String description;     //描述

    private List<Double> embedding;     //向量集

    private Position begin; //表示代码中的位置信息，例如行号和列号

    private Position end;   //表示代码中的位置信息

    private String language;    //语言

    private Integer repoId;     //项目仓库ID

    private String filePath;

    private String technologyStack;
}
