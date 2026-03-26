package com.demo;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;

/**
 * @author wuzhenhong
 * @date 2026/3/20 8:57
 */
public class ModelInterceptorTest extends ModelInterceptor {
    int count = 0;
    @Override
    public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
        System.out.println("ModelInterceptorTest invoke start......................");
        ModelResponse response = handler.call(request);
        System.out.println("ModelInterceptorTest invoke end......................");
        Object msg = response.getMessage();
        if (msg instanceof AssistantMessage assistantMessage
            && !assistantMessage.hasToolCalls() && ++count < 3) {
            // String id, String type, String name, String arguments
            ToolCall toolCall = new ToolCall("get_weather", "String", "get_weather", "北京");
            AssistantMessage message = AssistantMessage.builder()
                .content("调用工具吧 。。。。。。。。。")
                .toolCalls(Collections.singletonList(toolCall))
                .build();
            response = new ModelResponse(message);
        }

        return response;
    }

    @Override
    public String getName() {
        return "ModelInterceptorTest";
    }
}
