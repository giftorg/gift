package org.giftorg.codeanalyze.analyzer;

import org.giftorg.codeanalyze.code.Function;

import java.util.List;

public interface CodeAnalyzer {

    List<Function> getFuncList(String code);
}
