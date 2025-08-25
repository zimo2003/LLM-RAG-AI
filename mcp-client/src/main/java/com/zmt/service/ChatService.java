package com.zmt.service;

import com.zmt.entity.ChatEntity;
import org.springframework.ai.document.Document;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    Flux<String> streamStr(String question);

    void doChat(ChatEntity entity);

    void doSearch(ChatEntity chatEntity);

    void doInternet(ChatEntity chatEntity);
}
