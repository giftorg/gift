package com.gift.domain.escode;


import lombok.Data;

import java.io.Serializable;

@Data
public class Position implements Serializable {
    public Integer line;        //行
    public Integer column;      //列

    public Position() {
    }

    public Position(com.github.javaparser.Position position) {
        line = position.line;
        column = position.column;
    }
}