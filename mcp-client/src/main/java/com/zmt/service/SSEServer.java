package com.zmt.service;

import com.zmt.enums.SSEMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {

    private final static Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 连接
    public static SseEmitter connect(String userId) {
        // 设置超时时间为永不过时
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调方法
        sseEmitter.onTimeout(timeout(userId));
        sseEmitter.onCompletion(completion(userId));
        sseEmitter.onError(error(userId));
        emitters.put(userId, sseEmitter);
        return sseEmitter;
    }

    public static void sendMessage(String userId, String message, SSEMsgType msgType) {
        SseEmitter sseEmitter = emitters.get(userId);
        if (sseEmitter != null) {
            sendEmitterMessage(sseEmitter, userId, message, msgType);
        }
    }
    
    private static void sendEmitterMessage(SseEmitter sseEmitter, String userId, String message, SSEMsgType msgType) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .id(userId)
                .data(message)
                .name(msgType.getType());
        try {
            sseEmitter.send(event);
        } catch (IOException e) {
            // 更详细地处理不同类型的 IOException
            if (e instanceof SocketException) {
                log.info("SSE连接已重置，用户ID: {}，消息: {}", userId, e.getMessage());
            } else {
                log.error("发送SSE消息异常，用户ID: {}", userId, e);
            }
            // 移除失效的连接
            emitters.remove(userId);
        }
    }

    private static Consumer<Throwable> error(String userId) {
        return throwable -> {
            log.error("SSE连接发生错误，用户ID: {}", userId, throwable);
            // 移除用户连接
            emitters.remove(userId);
        };
    }

    private static Runnable completion(String userId) {
        return () -> {
            log.info("SSE连接完成，移除用户:{}连接", userId);
            emitters.remove(userId);
        };
    }

    public static Runnable timeout(String userId) {
        return () -> {
            log.info("SSE连接超时，移除用户:{}连接", userId);
            emitters.remove(userId);
        };
    }
}