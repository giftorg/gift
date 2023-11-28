package org.giftorg.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaUtilTest {

    @Test
    void testGetMethods() {
        List<String> methods;

        methods = JavaUtil.getMethods(ClassLoader.getSystemResource("ClassA.java").getPath());
        assertEquals(9, methods.size());

        methods = JavaUtil.getMethods(ClassLoader.getSystemResource("InterfaceA.java").getPath());
        assertEquals(3, methods.size());
    }
}
