package com.arfat.ai.promptTemplate;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/youtubers")
public class Youtubers {

    private final ChatClient chatClient;

    public Youtubers(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/popular")
    public String famousYoutubers(@RequestParam(value = "genre", defaultValue = "tech") String genre) {

        String message = """
                List 10 most famous youtubers in {genre} along with their current subscriber count. If you don't know 
                the answer, just say "I don't know". 
                """;
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("genre", genre));
        return this.chatClient.prompt(prompt)
                .call()
                .content();
    }
}
