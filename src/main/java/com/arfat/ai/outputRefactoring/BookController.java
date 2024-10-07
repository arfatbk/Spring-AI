package com.arfat.ai.outputRefactoring;

import com.arfat.ai.RAGAndEmbeddings.ReferenceDocLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDocLoader.class);
    private final ChatClient chatClient;

    public BookController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/author/{author}")
    public Map<String, Object> getAuthorSocialLinks(@PathVariable(value = "author") String author){
        var message = """
                Generate a list of links for the author {author}. Include the author's name as key and
                any social network links as the object.
                
                Just respond with the actual JSON, don't including any other text in your response except actual JSON.
                {format}
                """;

        var mapOutputParser = new MapOutputConverter();
        PromptTemplate promptTemplate = new PromptTemplate(message, Map.of("author", author, "format", mapOutputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        var generation = this.chatClient.prompt(prompt).call().content();
        log.info("Generation {}", generation);
        return mapOutputParser.convert(generation);
    }

}
