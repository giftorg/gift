package com.gift.domain.Vo;

import lombok.Data;
import org.giftorg.analyze.entity.Position;

/**
 * 返回前端
 */
@Data
public class FunctionVo {
    private String name;    //项目名

    private String source;  //源代码

    private String description;     //描述


    private Position begin; //表示代码中的位置信息，例如行号和列号

    private Position end;   //表示代码中的位置信息

    private String language;    //语言

    private Integer repoId;     //仓库ID

    private String filePath;     //URL

    private String technologyStack; //所用的技术

}
