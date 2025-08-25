package com.zmt.controller;

import com.zmt.common.Result;
import com.zmt.entity.WebSearchResponseEntity;
import com.zmt.service.InternetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internet")
public class InternetController {
    private final InternetService internetService;

    @GetMapping("/test")
    private WebSearchResponseEntity testSearch(String question) {
        return internetService.doInternetSearch(question);
    }
}
