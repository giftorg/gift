package com.gift;

//import com.gift.test.TestEs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import java.io.IOException;

@SpringBootApplication
public class Springboot01TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springboot01TestApplication.class, args);

        //测试
/*        TestEs testEs = new TestEs();
        try {
            testEs.createTest();
        } catch (IOException e) {

        }*/
    }

}
