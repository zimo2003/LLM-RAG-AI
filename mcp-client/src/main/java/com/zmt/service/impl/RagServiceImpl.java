package com.zmt.service.impl;

import com.zmt.common.ConsumerTextSplitter;
import com.zmt.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {
    private final RedisVectorStore redisVectorStore;


    @Override
    public void uploadRagDoc(String name, Resource resource) {
        // 专门读取文档用的
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", name);

        List<Document> documents = textReader.get();

        // 文档切割
        ConsumerTextSplitter tokenTextSplitter = new ConsumerTextSplitter();
//        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> splitList = tokenTextSplitter.apply(documents);
        System.out.println(splitList);
        redisVectorStore.add(splitList);
    }

}
