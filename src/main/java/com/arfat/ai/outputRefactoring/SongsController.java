package com.arfat.ai.outputRefactoring;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
public class SongsController {

    private final ChatClient chatClient;

    public SongsController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/artist/{artist}")
    public String songsByArtist(@PathVariable(value = "artist") String artist){
        var message = """
                Give me the Top 10 songs by the artist {artist}. If you don't know
                the answer, just say "I don't know".
                """;

        PromptTemplate promptTemplate = new PromptTemplate(message, Map.of("artist", artist));
        Prompt prompt = promptTemplate.create();
        return this.chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/v2/artist/{artist}")
    public List<String> songsByArtistFormatted(@PathVariable(value = "artist") String artist){
        var message = """
                Give me the Top 10 songs by the artist {artist}. If you don't know
                the answer, just say "I don't know".
                Just respond with the songs, dont including any other text.
                {format}
                """;

        ListOutputParser listOutputParser = new ListOutputParser(new DefaultConversionService());
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate
                .create(Map.of("artist", artist, "format", listOutputParser.getFormat()));
        var response =  this.chatClient.prompt(prompt).call().content();
        return listOutputParser.parse(response);
    }
}
