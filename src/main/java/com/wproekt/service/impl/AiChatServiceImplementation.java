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

import java.util.HashMap;
import java.util.Map;

@Service
public class AiChatServiceImplementation implements AiChatService {

    OpenAiChatModel chatModel;

    public AiChatServiceImplementation(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String read(JSONObject input) {

        Map<String,String> instructionMap = getInstructionOutput(input);

        String instruction = instructionMap.get("instruction");
        String output = instructionMap.get("output");

        Message message = getSystemText(input, instruction, output);
        UserMessage userMessage = new UserMessage(message.getContent());
        System.out.println(userMessage);

        Prompt prompt = new Prompt(userMessage);
        ChatResponse res = chatModel.call(prompt);

        return res.getResult().getOutput().toString();

    }

    private Map<String,String> getInstructionOutput(JSONObject input){
        String function = input.getString("function");

        Map<String, String> toReturn = new HashMap<>();



        if(function.equals("summarize")){

            toReturn.put("instruction", "Summarize the tasks into a concise sentence");
            toReturn.put("output",
                    "{'data': Structured Output }");
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
    private Message getSystemText(JSONObject input, String instruction, String output){
        String systemText;

        String typeCard = input.get("type").toString();
        String titleCard = input.get("title").toString();
        String dataCard = input.get("data").toString();

        systemText = """
                        You are a helpful AI assistant for a task monitoring application. You will get card
                        information from the user and you will have to do some transformation on the card.
                        Be concise and only output the needed data without additional comments.
                        
                        On the input you will get a card that contains {contains}.
                        {instruction}
                        
                        Card Title: '{title}',
                        Card Type: '{type}',
                        Card Data: '{data}',
                        
                        Return in this format:
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
