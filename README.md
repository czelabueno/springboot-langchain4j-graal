# Springboot 🍃 + Langchain4j 🐦 + OpenAI 🤖 + GraalVM JDK 21 🚀

## Build your own chatGPT inside a native image? Yes you can!

This sample app is part of a Java GenAI use case with OpenAI GPT LLM and it's part of a bigger personal project which look have multiple java samples developed with most popular Java AI frameworks (Spring AI, LangChain4j, Semantic Kernel, etc to developing amazing GenAI app using Java language.

![java-genai-frontend.png](images%2Fjava-genai-frontend.gif)

The frontend was built on angular. Here is the [repo](https://github.com/czelabueno/javagenai-frontend.git).

## Run it
### Add your OpenAI API Key
It's recommended create an environment var like this:
```shell
$ export OPENAI_API_KEY=********
```
Otherwise you can add it in the application properties.
```properties
openai.api.key=MyOpenAIApiKey
```

### Using the JVM
Make sure you're using GraalVM CE JDK 21

```shell
$ java -version
openjdk version "21" 2023-09-19
OpenJDK Runtime Environment GraalVM CE 21+35.1 (build 21+35-jvmci-23.1-b15)
OpenJDK 64-Bit Server VM GraalVM CE 21+35.1 (build 21+35-jvmci-23.1-b15, mixed mode, sharing)
```
Run springboot app
```shell
$ mvn clean spring-boot:run
```
Check your running app mode calling to `getNativeImage` API endpoint
```shell
## Send a first prompt
$ curl http://localhost:8080/openai/build-type
```

### Using AOT compilation and generate native imnage
First, create the native image config files using the agentlib. The project already have configured it in the `pom.xml`, Just run it like this:
```shell
$ mvn clean spring-boot:run
```
Test tha API endpoint. You can use `curl` as well
```shell
## Send a first prompt
$ curl -X POST -H "Content-type: application/json" -d '{"message":"Hi, my name is Carlos Zela"}' http://localhost:8080/openai

## Request event-stream responses
$ curl http://localhost:8080/openai/chat-stream -v
```
Copy the below files from `target/native-image` folder to `src/main/resources/META-INF/native-image` folder.

- proxy-config.json
- reflect-config.json
- resource-config.json
- serialization-config.json

Build the native image
```shell
$ mvn -Pnative native:compile
```

Run the naitve image
```shell
$ ./target/springboot-langchain4j-graal
```

Test the API endpoint again!




