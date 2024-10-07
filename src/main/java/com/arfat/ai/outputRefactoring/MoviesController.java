package com.arfat.ai.outputRefactoring;

import com.arfat.ai.RAGAndEmbeddings.ReferenceDocLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/movies")
public class MoviesController {
    private static final Logger log = LoggerFactory.getLogger(ReferenceDocLoader.class);

    private final ChatClient chatClient;

    public MoviesController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/{movie}")
    public String getMoviesCast(@PathVariable(value = "movie") String movie) {
        var message = """
                Return the star cast in the movie {movie}.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("movie", movie));
        log.info("Prompt {}", prompt);
        return this.chatClient.prompt(prompt).call().content();
    }

    @GetMapping("/v2/{movie}")
    public String getMoviesCastFormatted(@PathVariable(value = "movie") String movie) {
        var message = """
                Return the star cast in the movie {movie}.
                {format}
                """;

        BeanOutputConverter<Movie> beanOutputConverter = new BeanOutputConverter<>(Movie.class);
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("movie", movie, "format", beanOutputConverter.getFormat()));
        log.info("Prompt {}", prompt);
        return this.chatClient.prompt(prompt).call().content();
    }


    record Movie(String name, List<String> cast) {
    }
}