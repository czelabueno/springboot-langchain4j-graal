package com.czelabueno.springlangchain4jgraalvm.service;


import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class OpenAIChatService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private Assistant assistant;
    private StreamingAssistant streamingAssistant;

    interface Assistant {
        String chat(String message);
    }

    interface StreamingAssistant{
        TokenStream chat(String message);
    }

    @PostConstruct
    public void init(){
        var memory = TokenWindowChatMemory.withMaxTokens(1000, new OpenAiTokenizer("gpt-3.5-turbo"));
        assistant = AiServices.builder(Assistant.class)
                .chatMemory(memory)
                .chatLanguageModel(OpenAiChatModel.withApiKey(OPENAI_API_KEY))
                .build();

        streamingAssistant = AiServices.builder(StreamingAssistant.class)
                .chatMemory(memory)
                .streamingChatLanguageModel(OpenAiStreamingChatModel.withApiKey(OPENAI_API_KEY))
                .build();

    }

    public String chat(String message){
        return assistant.chat(message);
    }

    public Flux<String> chatStream(String message){
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        streamingAssistant.chat(message)
                .onNext(sink::tryEmitNext)
                .onComplete(c -> sink.tryEmitComplete())
                .onError(sink::tryEmitError)
                .start();
        return sink.asFlux();
    }


}
