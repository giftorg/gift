package org.giftorg.backed.service;

public interface ChatService {

    /**
     * GPT接口
     */
    String chat(String question) throws Exception;
}
