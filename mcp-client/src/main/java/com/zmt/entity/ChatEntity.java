package com.zmt.entity;

import lombok.Data;

@Data
public class ChatEntity {
    private String userId;
    private String question;
    // 不知道是干啥的
    private String botMsgId;
}
