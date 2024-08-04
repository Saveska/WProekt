//package com.wproekt.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.ai.openai.api.ApiUtils;
//import org.springframework.ai.openai.api.OpenAiApi;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@RequiredArgsConstructor
//@Configuration
//public class AiConfig {
//
//    @Value("${spring.ai.openai.api-key}")
//    private final String openAiApiKey;
//
//    @Bean
//    OpenAiApi openAiApi() {
//        final RestClient.Builder builder = RestClient.builder()
//                .requestFactory(new JdkClientHttpRequestFactory());
//        return new OpenAiApi(ApiUtils.DEFAULT_BASE_URL, openAiApiKey, builder);
//    }
//
//    @Bean
//    ChatClient chatClient(OpenAiApi openAiApi) {
//        return new OpenAiChatClient(openAiApi);
//    }
//
//    @Bean
//    EmbeddingClient embeddingClient(OpenAiApi openAiApi) {
//        return new OpenAiEmbeddingClient(openAiApi);
//    }
//}