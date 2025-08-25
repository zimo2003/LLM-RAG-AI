package com.zmt.controller;

import com.zmt.enums.SSEMsgType;
import com.zmt.service.SSEServer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SSEController {

    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(String userId, HttpServletRequest request) {
        log.info("用户连接成功：" + userId + "ip地址为{}", getClientIp(request));
        return SSEServer.connect(userId);
    }

    @GetMapping("/send")
    public void send(String userId, String message) {
        log.info("用户:" + userId + "发送消息");
        SSEServer.sendMessage(userId, message, SSEMsgType.MESSAGE);
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
