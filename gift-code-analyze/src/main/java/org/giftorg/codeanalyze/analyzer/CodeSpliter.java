package org.giftorg.codeanalyze.analyzer;

import org.giftorg.codeanalyze.code.Function;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public interface CodeSpliter extends Serializable {

    List<Function> splitFunctions(String code);

    List<Function> splitFunctions(InputStream in);
}
