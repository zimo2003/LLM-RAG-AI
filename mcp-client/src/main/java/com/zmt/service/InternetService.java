package com.zmt.service;

import com.zmt.entity.WebSearchResponseEntity;

public interface InternetService {

    WebSearchResponseEntity doInternetSearch(String question);
}
