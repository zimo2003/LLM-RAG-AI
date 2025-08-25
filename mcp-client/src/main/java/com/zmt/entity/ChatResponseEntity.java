package com.zmt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponseEntity {
    private String message;
    private String botMsgId;
}
