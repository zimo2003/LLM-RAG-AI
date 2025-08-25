package com.zmt.entity;

import lombok.Data;

@Data
public class WebSearchEntity {
    private String title;
    private String url;
    private String content;
    private double score;
}
