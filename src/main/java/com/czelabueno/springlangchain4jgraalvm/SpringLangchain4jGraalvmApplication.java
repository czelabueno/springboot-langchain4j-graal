package com.czelabueno.springlangchain4jgraalvm;

import com.czelabueno.springlangchain4jgraalvm.service.OpenAIChatService;
import dev.ai4j.openai4j.chat.*;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.TokenStream;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.NativeDetector;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RegisterReflectionForBinding({
		OpenAIChatService.class,
		TokenStream.class,
		AssistantMessage.class,
		AiMessage.class,
		ChatCompletionRequest.class,
		ChatCompletionResponse.class,
		ChatCompletionModel.class,
		ChatCompletionChoice.class,
		ChatMessage.class,
		Delta.class,
		Content.class,
		Function.class,
		FunctionCall.class,
		Parameters.class,
		Response.class,
		Tool.class,
		ToolCall.class,
		ToolChoice.class,
		ToolMessage.class,
		ToolType.class,
		UserMessage.class
})
// Registrar langchain4j classes to reflect is not enough for AOT compilation. Use graal java agentlib to generate the rest of dynamic features.
public class SpringLangchain4jGraalvmApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringLangchain4jGraalvmApplication.class, args);
	}

}

@RestController
@RequestMapping("openai")
@CrossOrigin
class ChatController {

	@Autowired
	private OpenAIChatService openAIChatService;

	private final Map<Integer, String> messages = new HashMap<>();
	private final AtomicInteger counter = new AtomicInteger(1);


	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public void sendMessage(@RequestBody String message){
		System.out.println("user message: " + message);
		messages.put(counter.getAndIncrement(), message);
	}

	@GetMapping(path="chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> streamChatMessages(){
		String lastMessage = "";
		lastMessage = (counter.get() <= 1) ? "Hi, my name is Carlos" : messages.get(counter.get() -1);
		System.out.println("stream message: " + lastMessage);
		return openAIChatService.chatStream(lastMessage);
	}

	@PostMapping(path="chat", consumes = MediaType.APPLICATION_JSON_VALUE)
	public String chatMessage(@RequestBody String message){
		System.out.println("chat message: " + message);
		return openAIChatService.chat(message);
	}

	@GetMapping("build-type")
	public String isNative(){
		return "Spring boot is running as native image? " + NativeDetector.inNativeImage();
	}

}
