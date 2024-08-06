package com.wproekt.service.impl;

import org.json.JSONObject;
import com.wproekt.service.AiChatService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiChatServiceImplementation implements AiChatService {

    OpenAiChatModel chatModel;


    public AiChatServiceImplementation(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Flux<ChatResponse> read(JSONObject input) {

        Map<String, String> instructionMap = getInstructionOutput(input);

        String instruction = instructionMap.get("instruction");
        String output = instructionMap.get("output");

        Message message = getSystemText(input, instruction, output);
        UserMessage userMessage = new UserMessage(message.getContent());
        System.out.println(userMessage);

        Prompt prompt = new Prompt(userMessage);


        return chatModel.stream(prompt);

    }

    private Map<String, String> getInstructionOutput(JSONObject input) {
        String function = input.getString("function");

        Map<String, String> toReturn = new HashMap<>();


        switch (function) {
            case "summarize" -> {
                toReturn.put("instruction", "Summarize the tasks into a concise sentence.");
                toReturn.put("output", "{\"type\": \"note\", \"data\": \"Summarized sentence\"}");
            }
            case "findItems" -> {
                toReturn.put("instruction", "Identify and list the action items from the given data.");
                toReturn.put("output", "{\"type\": \"task\", \"data\": [{\"taskContent\": \"...\", \"finished\": true/false}]}");
            }
            case "explain" -> {
                toReturn.put("instruction", "Explain the given data in simpler terms.");
                toReturn.put("output", "{\"type\": \"{type}\", \"data\": \"Simplified explanation\"}");
            }
            case "improve" -> {
                toReturn.put("instruction", "Improve the quality of the given writing.");
                toReturn.put("output", "{\"type\": \"{type}\", \"data\": \"Improved data\"}");
            }
            case "spelling" -> {
                toReturn.put("instruction", "Correct the spelling and grammar of the given data.");
                toReturn.put("output", "{\"type\": \"{type}\", \"data\": \"Corrected data\"}");
            }
            case "shorter" -> {
                toReturn.put("instruction", "Shorten the given data while retaining its meaning.");
                toReturn.put("output", "{\"type\": \"{type}\", \"data\": \"Shortened data\"}");
            }
            case "brainstorm" -> {
                toReturn.put("instruction", "Generate new ideas based on the given data.");
                toReturn.put("output", "{\"type\": \"task\", \"data\": [{\"taskContent\": \"New task idea\", \"finished\": false}, ...]}");
            }
            default -> throw new IllegalArgumentException("Unsupported function: " + function);
        }

        //1.findItems
        //       [
        //                {
        //                "taskContent": "...",
        //                "finished" : bool,
        //                },
        //                {
        //                 "taskContent": "...",
        //                 "finished" : bool,
        //                 },
        //
        //
        //
        //        ]

        //2.explain
        //3.improve
        //4.spelling
        //5.shorter
        //6.brainstorm

        return toReturn;
    }

    private Message getSystemText(JSONObject input, String instruction, String output) {
        String systemText;

        String typeCard = input.get("type").toString();
        String titleCard = input.get("title").toString();
        String dataCard = input.get("data").toString();

        systemText = """
                    You are a helpful AI assistant for a task monitoring application. Your role is to transform 
                    the provided card information based on specific instructions. Be concise and output only
                    the required data without additional comments.
                    
                    You will receive a card containing {contains}.
                    {instruction}
                    
                    Card Title: '{title}',
                    Card Type: '{type}',
                    Card Data: '{data}',
                    
                    You must return the response in the following JSON format:
                    {output}
                """;
        String contains = typeCard.equals("task") ? "tasks" : "text";


        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);

        Message message = systemPromptTemplate.createMessage(
                Map.of("contains", contains,
                        "instruction", instruction,
                        "title", titleCard,
                        "type", typeCard,
                        "data", dataCard,
                        "output", output));

        return message;
    }

}
