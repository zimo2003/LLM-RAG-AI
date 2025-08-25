package com.zmt.enums;

import lombok.Getter;

@Getter
public enum SSEMsgType {
    MESSAGE("message", "单次发送消息"),
    ADD("add", "消息追加，流式推送"),
    FINISH("finish", "结束"),
    CUSTOMER("customer", "自定义消息");

    private final String type;
    private final String desc;

    SSEMsgType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
