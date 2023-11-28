package org.giftorg.codeanalyze.code;

import lombok.Data;

@Data
public class Position {
    public Integer line;
    public Integer column;

    public Position() {
    }

    public Position(com.github.javaparser.Position position) {
        line = position.line;
        column = position.column;
    }
}
