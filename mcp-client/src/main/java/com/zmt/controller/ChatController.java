package com.zmt.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.zmt.anno.Log;
import com.zmt.entity.ChatEntity;
import com.zmt.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;
    /**
     * 聊天接口(流式)
     *
     * @param question
     * @return
     */
    @GetMapping("/stream")
    @Log
    public Flux<String> streamChat(String question, HttpServletResponse response, HttpServletRequest request) {
        log.warn("当前请求地址：{}，参数：{}", getClientIp(request), question);
        response.setCharacterEncoding("UTF-8");
        return chatService.streamStr(question);
    }

    @PostMapping("/doChat")
    public void doChat(@RequestBody ChatEntity entity, HttpServletRequest request) {
        log.warn("当前请求地址：{}，参数：{}", getClientIp(request), entity.getQuestion());
        chatService.doChat(entity);
    }

    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletRequest request) {
        log.warn("当前请求地址：{}，参数：{}", getClientIp(request), chatEntity.getQuestion());
        chatService.doSearch(chatEntity);
    }

    @PostMapping("/doInternet")
    public void doInternet(@RequestBody ChatEntity chatEntity, HttpServletRequest request) {
        log.warn("当前请求地址：{}，参数：{}", getClientIp(request), chatEntity.getQuestion());
        chatService.doInternet(chatEntity);
    }


    private String getClientIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0];
                }
                return ip.trim();
            }
        }

        return request.getRemoteAddr();
    }
}
