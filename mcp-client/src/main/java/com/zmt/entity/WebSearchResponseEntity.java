package com.zmt.entity;

import lombok.Data;

import java.util.List;

@Data
public class WebSearchResponseEntity {
    private String query;
    private List<WebSearchEntity> results;
}
