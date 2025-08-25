package com.zmt.controller;

import com.zmt.common.Result;
import com.zmt.entity.ChatEntity;
import com.zmt.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagController {
    private final RagService ragService;

    @PostMapping("/uploadRagDoc")
    public Result<String> uploadRagDoc(MultipartFile file) {
        String name = file.getOriginalFilename();
        Resource resource = file.getResource();
        ragService.uploadRagDoc(name,resource);
        return Result.ok("上传成功");
    }
}
