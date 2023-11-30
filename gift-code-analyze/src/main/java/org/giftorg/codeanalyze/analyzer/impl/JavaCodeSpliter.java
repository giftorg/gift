package org.giftorg.codeanalyze.analyzer.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.giftorg.codeanalyze.analyzer.CodeSpliter;
import org.giftorg.codeanalyze.code.Function;
import org.giftorg.codeanalyze.code.Position;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGLM;
import org.giftorg.common.bigmodel.impl.ChatGPT;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaCodeSpliter implements CodeSpliter {
    private final static String LANGUAGE_JAVA = "java";

    @Override
    public List<Function> splitFunctions(String file) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(file));
            return splitFunctions(cu);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Function> splitFunctions(InputStream in) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(in);
            return splitFunctions(cu);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Function> splitFunctions(CompilationUnit cu) {
        try {
            List<Function> result = new ArrayList<>();

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                AtomicBoolean isImpl = new AtomicBoolean(false);

                // 判断是否为未实现的接口方法
                method.getParentNode().ifPresent(node -> {
                    if (node instanceof ClassOrInterfaceDeclaration) {
                        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
                        if (!classOrInterfaceDeclaration.isInterface() || method.isStatic()) {
                            isImpl.set(true);
                        }
                    } else {
                        isImpl.set(true);
                    }
                });
                // 跳过未实现的接口方法
                if (!isImpl.get()) {
                    return;
                }

                Function func = new Function();
                func.setName(method.getNameAsString());
                func.setSource(method.toString());
                func.setBegin(method.getBegin().isPresent() ? new Position(method.getBegin().get()) : null);
                func.setEnd(method.getEnd().isPresent() ? new Position(method.getEnd().get()) : null);
                func.setLanguage(LANGUAGE_JAVA);
                result.add(func);
            });

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
