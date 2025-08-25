package com.zmt.service.impl;

import cn.hutool.json.JSONUtil;
import com.zmt.entity.ChatEntity;
import com.zmt.entity.ChatResponseEntity;
import com.zmt.entity.WebSearchEntity;
import com.zmt.entity.WebSearchResponseEntity;
import com.zmt.enums.SSEMsgType;
import com.zmt.service.ChatService;
import com.zmt.service.InternetService;
import com.zmt.service.SSEServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private ChatClient client;

    @Autowired
    private RedisVectorStore redisVectorStore;

    @Autowired
    private InternetService internetService;

    // AI默认提示词
    private static final String DEFAULT_PROMPT = "你是一个精通Java的程序员，你的名称叫做zmbwcx2003";

    // Rag知识库提示词
    private final static String RAG_SEARCH = """
            基于上下文的知识库回答问题：
            【上下文】
            {context}
            
            【问题】
            {question}
            
            【输出】
            如果知识库没有相关知识请回复：似乎知识库中还没有这个问题的答案，上传一下文档吧先
            如果存在请回复相关内容，不相关的内容请省略
            """;

    // 联网搜索提示词
    private final static String SEARXNG_SEARCH = """
            基于网络搜索的内容，综合理解回答用户提出的问题：
            【上下文】
            {context}
            
            【问题】
            {question}
            
            【输出】
            如果没有查询到相关内容请回复：似乎来到了知识的荒原，换一个问题试试吧
            如果存在请回复相关问题答案，不相关的内容请省略
            """;


    public ChatServiceImpl(ChatClient.Builder clientBuilder) {
        this.client = clientBuilder.defaultSystem(DEFAULT_PROMPT).build();
    }

    @Override
    public Flux<String> streamStr(String question) {
        return client.prompt(question).stream().content();
    }

    @Override
    public void doChat(ChatEntity entity) {
        String question = entity.getQuestion();
        String userId = entity.getUserId();
        String botMsgId = entity.getBotMsgId();

        Flux<String> stringFlux = client.prompt(question).stream().content();

//        // 用于累积完整消息的引用
//        AtomicReference<String> fullMessage = new AtomicReference<>("");
//
//        // 使用 subscribe 替代 toList，以更好地处理流式数据
//        stringFlux.subscribe(
//            content -> {
//                // 累积完整消息
//                fullMessage.set(fullMessage.get() + content);
//                // 发送每个数据块到客户端
//                SSEServer.sendMessage(userId, content, SSEMsgType.ADD);
//            },
//            error -> {
//                // 处理流中的错误
//                log.error("处理AI响应时发生错误，用户ID: {}, 问题: {}", userId, question, error);
//                // 发送格式化的错误消息
//                Map<String, String> errorMsg = new HashMap<>();
//                errorMsg.put("message", "抱歉，处理您的请求时发生错误。");
//                errorMsg.put("botMsgId", botMsgId);
//                try {
//                    String errorMsgJson = objectMapper.writeValueAsString(errorMsg);
//                    SSEServer.sendMessage(userId, errorMsgJson, SSEMsgType.FINISH);
//                } catch (Exception jsonException) {
//                    log.error("序列化错误消息失败", jsonException);
//                    SSEServer.sendMessage(userId, "抱歉，处理您的请求时发生错误。", SSEMsgType.FINISH);
//                }
//            },
//            () -> {
//                // 流完成时的通知
//                log.info("AI响应完成，用户ID: {}, 问题: {}", userId, question);
//                // 发送完成信号，包含完整消息
//                Map<String, String> finishMsg = new HashMap<>();
//                finishMsg.put("message", fullMessage.get());
//                finishMsg.put("botMsgId", botMsgId);
//                try {
//                    String finishMsgJson = objectMapper.writeValueAsString(finishMsg);
//                    SSEServer.sendMessage(userId, finishMsgJson, SSEMsgType.FINISH);
//                } catch (Exception jsonException) {
//                    log.error("序列化完成消息失败", jsonException);
//                    SSEServer.sendMessage(userId, fullMessage.get(), SSEMsgType.FINISH);
//                }
//            }
//        );
        List<String> list = stringFlux.toStream().map(content -> {
            String message = content.toString();
            SSEServer.sendMessage(userId, message, SSEMsgType.ADD);
            return message;
        }).toList();
        String fullMessage = list.stream().collect(Collectors.joining());
        ChatResponseEntity responseEntity = new ChatResponseEntity(fullMessage, botMsgId);
        SSEServer.sendMessage(userId, JSONUtil.toJsonStr(responseEntity), SSEMsgType.FINISH);
    }

    @Override
    public void doSearch(ChatEntity chatEntity) {
        String question = chatEntity.getQuestion();
        String userId = chatEntity.getUserId();
        String botMsgId = chatEntity.getBotMsgId();

        List<Document> documents = redisVectorStore.similaritySearch(question);
        // 从向量库中获取相关文档
        if (documents != null || documents.size() > 0) {
            // 构建提示词
            String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n"));
            context = RAG_SEARCH.replace("{context}", context).replace("{question}", question);
            Prompt prompt = new Prompt(context);
            Flux<String> stringFlux = client.prompt(prompt).stream().content();
            List<String> list = stringFlux.toStream().map(content -> {
                String message = content.toString();
                SSEServer.sendMessage(userId, message, SSEMsgType.ADD);
                return message;
            }).toList();
            String fullMessage = list.stream().collect(Collectors.joining());
            ChatResponseEntity responseEntity = new ChatResponseEntity(fullMessage, botMsgId);
            SSEServer.sendMessage(userId, JSONUtil.toJsonStr(responseEntity), SSEMsgType.FINISH);
            log.info("用户ID: {}, 问题: {}回答完毕", userId, question);
        }
    }

    @Override
    public void doInternet(ChatEntity chatEntity) {
        String question = chatEntity.getQuestion();
        String userId = chatEntity.getUserId();
        String botMsgId = chatEntity.getBotMsgId();

        WebSearchResponseEntity webSearchResponseEntity = internetService.doInternetSearch(question);
        List<WebSearchEntity> searchResults = webSearchResponseEntity.getResults();
        System.out.println(searchResults);
        if (searchResults != null) {
            StringBuilder stringBuilder = new StringBuilder();
            searchResults.forEach(searchResult -> {
                stringBuilder.append(
                        String.format("来源：%s\n摘要：%s\n\n",
                                searchResult.getUrl(),
                                searchResult.getContent()
                        ));
            });
            String promptStr = SEARXNG_SEARCH.replace("{context}", stringBuilder)
                    .replace("{question}", question);
            System.out.println(promptStr);

            Prompt prompt = new Prompt(promptStr);
            Flux<String> stringFlux = client.prompt(prompt).stream().content();
            List<String> list = stringFlux.toStream().map(content -> {
                String message = content.toString();
                SSEServer.sendMessage(userId, message, SSEMsgType.ADD);
                return message;
            }).toList();
            String fullMessage = list.stream().collect(Collectors.joining());
            ChatResponseEntity responseEntity = new ChatResponseEntity(fullMessage, botMsgId);
            SSEServer.sendMessage(userId, JSONUtil.toJsonStr(responseEntity), SSEMsgType.FINISH);
            log.info("用户ID: {}, 问题: {}回答完毕", userId, question);
        }

    }
}