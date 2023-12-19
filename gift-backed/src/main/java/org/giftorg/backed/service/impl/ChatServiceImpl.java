package org.giftorg.backed.service.impl;

import org.giftorg.backed.service.ChatService;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGPT;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ChatServiceImpl implements ChatService {

    private static final BigModel gpt = new ChatGPT();

    @Override
    public String chat(String question) throws Exception {
        ArrayList<BigModel.Message> messages = new ArrayList<>();
        messages.add(new BigModel.Message("user", question));

        return gpt.chat(messages);
    }

}
