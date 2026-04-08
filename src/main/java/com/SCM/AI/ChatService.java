package com.SCM.AI;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.List;

@Service
public class ChatService {

    private final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private final String API_KEY = System.getenv("GROQ_API_KEY");

    public String getAIReply(String message) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
        {
          "model": "llama-3.1-8b-instant",
          "messages": [
              {
                "role": "system",
                "content": "You are a helpful AI assistant. Give short, clear, and concise answers in 1-2 sentences."
              },
            { "role": "user", "content": "%s" }
          ]
        }
        """.formatted(message);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map body = response.getBody();

        List choices = (List) body.get("choices");
        Map first = (Map) choices.get(0);
        Map messageMap = (Map) first.get("message");

        return messageMap.get("content").toString();
    }
}