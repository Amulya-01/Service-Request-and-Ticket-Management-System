package com.example.TicketManagement.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public AIService(ChatClient.Builder chatClientBuilder,
                     VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public String askQuestion(String question) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(2)
                .build();

        List<Document> documents =
                vectorStore.similaritySearch(searchRequest);

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        return chatClient.prompt()
                .system("""
                        You are an IT Support Assistant.

                        Answer the user's question using the provided
                        IT knowledge base context.

                        If the knowledge base does not contain enough
                        information, clearly say that the knowledge
                        base does not contain enough information.

                        Do not invent troubleshooting steps.

                        Knowledge Base Context:
                        """ + context)
                .user(question)
                .call()
                .content();
    }
}