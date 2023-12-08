/**
 * Copyright 2023 GiftOrg Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.analyze.codespliter.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.giftorg.analyze.codespliter.CodeSpliter;
import org.giftorg.analyze.entity.Function;
import org.giftorg.analyze.entity.Position;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java 代码拆分器
 */
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

    /**
     * 按 class 拆分代码
     */
    private List<Function> splitFunctions(CompilationUnit cu) {
        try {
            List<Function> result = new ArrayList<>();

            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration cls : classes) {
                if (cls.isInterface()) continue;

                Function func = new Function();
                func.setName(cls.getNameAsString());
                func.setSource(cls.toString());
                func.setBegin(cls.getBegin().isPresent() ? new Position(cls.getBegin().get()) : null);
                func.setEnd(cls.getEnd().isPresent() ? new Position(cls.getEnd().get()) : null);
                func.setLanguage(LANGUAGE_JAVA);
                result.add(func);
            }

            /* 按函数拆分版本（已弃用）
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
             */

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
