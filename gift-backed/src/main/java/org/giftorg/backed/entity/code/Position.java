package org.giftorg.backed.entity.code;


import lombok.Data;

import java.io.Serializable;

@Data
public class Position implements Serializable {
    public Integer line;

    public Integer column;

    public Position() {
    }

    public Position(com.github.javaparser.Position position) {
        line = position.line;
        column = position.column;
    }
}