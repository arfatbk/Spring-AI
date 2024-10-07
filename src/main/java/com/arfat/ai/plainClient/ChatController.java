package com.arfat.ai.plainClient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("")
    public String joke(){
        return this.chatClient
                .prompt()
                .user("Please tell me a joke")
                .call()
                .content();
    }
}
