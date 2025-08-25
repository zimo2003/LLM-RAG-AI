package com.zmt.service;

import com.zmt.entity.ChatEntity;
import org.springframework.core.io.Resource;

public interface RagService {
    void uploadRagDoc(String name, Resource resource);

}
