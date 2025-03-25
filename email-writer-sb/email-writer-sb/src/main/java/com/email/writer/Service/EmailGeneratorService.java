package com.email.writer.Service;

import com.email.writer.DTO.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private final WebClient webClient;
    @Autowired
    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    public String generateEmailReply(EmailRequest emailRequest){
        String prompt=buildPrompt(emailRequest);
        Map<String ,Object> requestBody= Map.of("contents",new Object[]{
                Map.of("parts",new Object[]{
                        Map.of("text",prompt)
                })
        });
        String response=webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return extractResponseContent(response);

    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }
        catch (Exception e){
            return "Error processing request:"+e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt=new StringBuilder();
        prompt.append("You are an AI assistant that generates professional email replies.  \n" +
                "Here is an email I received: \n");
        prompt.append(emailRequest.getEmailContent());
        if(emailRequest.getTone()!=null && !emailRequest.getTone().isEmpty()){
            prompt.append("I want to reply in a ").append(emailRequest.getTone()).append(" tone");
        }
        prompt.append("Please craft a well-structured, natural-sounding response that maintains a professional and appropriate tone.  \n" +
                "Make sure the reply is clear, concise, and addresses the key points mentioned in the received email.\n");
        prompt.append("Do not give any other explanation just give reply for mail and do not include closing and signature also do not give subject line");
        System.out.println(prompt.toString());
        return prompt.toString();
    }

}
