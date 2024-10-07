package com.arfat.ai.RAGAndEmbeddings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/plant")
public class PlantController {

    private static final Logger log = LoggerFactory.getLogger(PlantController.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/plant-assistant.st")
    private Resource systemPrompt;

    public PlantController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("")
    public String getAnswers(@RequestParam(value = "q", defaultValue = "Summarize Plant Phenotyping") String q){

        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        Prompt prompt = promptTemplate.create(Map.of("input", q, "documents", String.join("\n", findSimilarDocuments(q))));

        return this.chatClient.prompt(prompt)
                .call()
                .content();
    }

    private List<String> findSimilarDocuments(String q) {
        List<Document> documents = this.vectorStore.similaritySearch(SearchRequest.query(q).withTopK(10));
        return documents.stream().map(Document::getContent).toList();
    }
}
