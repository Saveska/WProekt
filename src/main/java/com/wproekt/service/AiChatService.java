package com.wproekt.service;

import org.json.JSONObject;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface AiChatService {

    Flux<ChatResponse> read(JSONObject input);
}
