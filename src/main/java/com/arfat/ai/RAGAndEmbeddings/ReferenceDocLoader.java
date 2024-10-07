package com.arfat.ai.RAGAndEmbeddings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ReferenceDocLoader {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDocLoader.class);

    @Value("classpath:/data/Plant-Phenotyping.pdf")
    private Resource plantPhenotypingDoc;

    @Value("vectorstore.json")
    private String vectorStoreName;


    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        File vectorStoreFile = getVectorStoreFile();
        if(vectorStoreFile.exists()){
            log.info("vector store file found in classpath");
            simpleVectorStore.load(vectorStoreFile);
        } else {
            this.init(simpleVectorStore, vectorStoreFile);
        }

        return simpleVectorStore;
    }

    private void init(SimpleVectorStore vectorStore, File vectorStoreFile) {
        log.info("Loading vector store");

        var config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                        .withNumberOfBottomTextLinesToDelete(0)
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build();
        var pdfReader = new PagePdfDocumentReader(plantPhenotypingDoc, config);
        var textSplitter = new TokenTextSplitter();
        vectorStore.add(textSplitter.apply(pdfReader.get()));
        vectorStore.save(vectorStoreFile);

        log.info("vector store is ready");
    }


    private File getVectorStoreFile(){
        Path path = Paths.get("src","main", "resources", "data");
        var absolutePath = path.toFile().getAbsolutePath() + "/" + vectorStoreName;
        return new File(absolutePath);
    }
}
