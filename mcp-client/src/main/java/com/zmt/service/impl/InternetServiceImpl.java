package com.zmt.service.impl;

import cn.hutool.json.JSONUtil;
import com.zmt.entity.WebSearchEntity;
import com.zmt.entity.WebSearchResponseEntity;
import com.zmt.service.InternetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternetServiceImpl implements InternetService {
    @Value("${internet.web-search.searxng.url}")
    private String URL;
    @Value("${internet.web-search.searxng.count}")
    private Integer COUNT;

    private final OkHttpClient okHttpClient;

    @Override
    public WebSearchResponseEntity doInternetSearch(String question) {
        HttpUrl httpUrl = HttpUrl.get(URL).newBuilder()
                // 请求参数固定，具体参考SearXNG的文档
                .addQueryParameter("q", question)
                .addQueryParameter("format", "json")
                .build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();
        log.info("搜索的url地址为：" + httpUrl.url());
        try (Response response = okHttpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new RuntimeException("请求失败: HTTP " + response.code());
            ResponseBody body = response.body();
            if (body != null) {
                String bodyStr = body.string();
                System.out.println("搜索结果："+bodyStr);
                return limitResult(JSONUtil.toBean(bodyStr, WebSearchResponseEntity.class));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new WebSearchResponseEntity();
    }

    /**
     * 限制结果数量
     *
     * @param bean
     * @return
     */
    private WebSearchResponseEntity limitResult(WebSearchResponseEntity bean) {
        List<WebSearchEntity> list = bean.getResults().subList(0, Math.min(bean.getResults().size(), COUNT))
                .parallelStream()
                .sorted(Comparator.comparingDouble(WebSearchEntity::getScore).reversed())
                .limit(COUNT).toList();
        bean.setResults(list);
        return bean;
    }
}
