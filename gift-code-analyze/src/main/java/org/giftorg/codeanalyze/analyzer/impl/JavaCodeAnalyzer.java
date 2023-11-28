package org.giftorg.codeanalyze.analyzer.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.giftorg.codeanalyze.analyzer.CodeAnalyzer;
import org.giftorg.codeanalyze.code.Function;
import org.giftorg.codeanalyze.code.Position;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGLM;
import org.giftorg.common.bigmodel.impl.ChatGPT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaCodeAnalyzer implements CodeAnalyzer {
    private final static String LANGUAGE_JAVA = "java";

    @Override
    public List<Function> getFuncList(String file) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(file));
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

                // 获取函数的描述信息
                BigModel gpt = new ChatGPT();
                try {
                    String description = gpt.chat(new ArrayList<BigModel.Message>() {{
                        // 为用户输入的函数写一行不超过50字的中文注释，描述函数的作用。
                        // Write a Chinese comment in one line, not exceeding 50 characters, for the user-inputted function, describing the function's purpose.
                        add(new BigModel.Message("system", "Write a Chinese comment in one line, not exceeding 50 characters, for the user-inputted function, describing the function's purpose.\nInput example: \"public static void add(int a, int b) { return a + b; }\"\nOutput example: \"计算两位整数的和\""));
                        add(new BigModel.Message("user", method.toString()));
                    }});
                    func.setDescription(description);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // 将函数描述向量化
                BigModel glm = new ChatGLM();
                try {
                    List<Double> embedding = glm.textEmbedding(func.getDescription());
                    func.setEmbedding(embedding);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                result.add(func);

                try {
                    Thread.sleep(19000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
