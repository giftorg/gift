package org.giftorg.spark.bigmodel;

import lombok.Data;
import lombok.ToString;

import java.util.List;

public interface BigModel {

    String chat(List<Message> messages) throws Exception;

    /**
     * 大模型消息结构
     */
    @Data
    @ToString
    class Message {
        public String role;
        public String content;

        public Message(String user, String hello) {
            this.role = user;
            this.content = hello;
        }
    }
}
