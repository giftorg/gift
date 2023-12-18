package com.gift.service.impl;

import com.gift.service.ChatService;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.bigmodel.impl.ChatGPT;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public class ChatServiceImpl extends ChatGPT implements ChatService {


    @Override
    public String chat(String question){
        ArrayList<BigModel.Message> messages = new ArrayList<>();
        messages.add(new BigModel.Message("user", question));

        String chat = super.chat(messages); //得到回答

//        Stream<String> answerStream = Stream.of(chat);

//        System.out.println(answerStream);

        return chat;
    }

}
