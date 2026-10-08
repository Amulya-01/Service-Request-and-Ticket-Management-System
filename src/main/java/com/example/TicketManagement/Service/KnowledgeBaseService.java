package com.example.TicketManagement.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeBaseService {

    private final VectorStore vectorStore;

    public KnowledgeBaseService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void loadKnowledgeBase() throws IOException {

        PathMatchingResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();

        Resource[] resources =
                resolver.getResources("classpath:/knowledge/*.txt");

        List<Document> documents = new ArrayList<>();

        for (Resource resource : resources) {

            String content = resource.getContentAsString(StandardCharsets.UTF_8);

            Document document = new Document(
                    content,
                    java.util.Map.of(
                            "source",
                            resource.getFilename()
                    )
            );

            documents.add(document);
        }

        vectorStore.add(documents);

        System.out.println("Knowledge Base loaded: " + documents.size() + " documents");
    }
}