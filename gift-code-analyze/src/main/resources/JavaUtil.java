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