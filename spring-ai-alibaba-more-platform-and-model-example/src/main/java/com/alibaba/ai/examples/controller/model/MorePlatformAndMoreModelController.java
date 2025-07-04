/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.ai.examples.controller.model;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * @author liu-657667
 * @author <a href="mailto:liu-657667@qq.com">liu-657667</a>
 */

@RestController
@RequestMapping("no-platform")
public class MorePlatformAndMoreModelController {

    private final ChatModel dashScopeChatModel;

    private final ChatModel ollamaChatModel;
    private final ChatModel openAIChatModel;

    private final Set<String> modelList = Set.of(
            "deepseek-r1",
            "deepseek-v3",
            "qwen-plus",
            "qwen-max"
    );
    private final Set<String> ollamaModelList = Set.of("deepseek-r1:1.5b");
    private final Set<String> openaiModelList = Set.of("gpt-4.1");

    Map<String, Set<String>> modelMap = Map.of(
            "dashscope", modelList,
            "ollama", ollamaModelList,
            "openai", openaiModelList
    );
    /**
    * @Description: @Qualifier注解是用来指定注入的bean的名字，value值需要到源码中查看，如不对应会报错：NoSuchBeanDefinitionException
     * 如openAiChatModel的路径见：org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.java
    */
    public MorePlatformAndMoreModelController(
            @Qualifier("dashscopeChatModel") ChatModel dashScopeChatModel,
            @Qualifier("ollamaChatModel") ChatModel OllamaChatModel,
            @Qualifier("openAiChatModel") ChatModel openAIChatModel
    ) {
        this.dashScopeChatModel = dashScopeChatModel;
        this.ollamaChatModel = OllamaChatModel;
        this.openAIChatModel = openAIChatModel;
    }

    @GetMapping("/{platform}/{models}/{prompt}")
    public String chat(
            @PathVariable("platform") String platform,
            @PathVariable("models") String models,
            @PathVariable("prompt") String prompt
    ) {

        System.out.println("===============================================");
        System.out.println("DashScope Model：" + dashScopeChatModel.toString());
        System.out.println("Ollama Model：" + ollamaChatModel.toString());
        System.out.println("OpenAI Model：" + openAIChatModel.toString());
        System.out.println("===============================================");
        if (CollectionUtils.isEmpty(modelMap.get(platform)) || !modelMap.get(platform).contains(models)) {
            return "model not exist";
        }
        ChatOptions runtimeOptions = ChatOptions.builder().model(models).build();
        if ("dashscope".equals(platform)) {
            return dashScopeChatModel.call(new Prompt(prompt, runtimeOptions))
                    .getResult().getOutput().getText();
        }

        if ("ollama".equals(platform)) {
            return ollamaChatModel.call(new Prompt(prompt, runtimeOptions))
                    .getResult().getOutput().getText();
        }

        if ("openAI".equals(platform)) {
            return openAIChatModel.call(new Prompt(prompt, runtimeOptions))
                    .getResult().getOutput().getText();
        }
        return "Error ...";
    }

}
