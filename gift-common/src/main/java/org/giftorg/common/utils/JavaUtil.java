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

package org.giftorg.common.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class JavaUtil {

    public static List<String> getMethods(String javaFile) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(javaFile));
            List<String> result = new ArrayList<>();
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                System.out.println(method.getBegin());
//                System.out.println(method.getEnd());
                method.getParentNode().ifPresent(node -> {
                    if (node instanceof ClassOrInterfaceDeclaration) {
                        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) node;
                        if (!classOrInterfaceDeclaration.isInterface() || method.isStatic()) {
                            result.add(method.toString());
                        }
                    } else {
                        result.add(method.toString());
                    }
                });
            });
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        getMethods(Objects.requireNonNull(JavaUtil.class.getClassLoader().getResource("XingHuo.java")).getPath());
        getMethods(Objects.requireNonNull(JavaUtil.class.getClassLoader().getResource("BigModel.java")).getPath());
        getMethods(Objects.requireNonNull(JavaUtil.class.getClassLoader().getResource("InterfaceA.java")).getPath());

    }
}