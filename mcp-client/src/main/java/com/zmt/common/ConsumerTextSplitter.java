package com.zmt.common;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.List;

public class ConsumerTextSplitter extends TokenTextSplitter {
    @Override
    protected List<String> splitText(String text) {
        String[] splitList = text.split("\\s*\\R");
        return List.of(splitList);
    }
}
