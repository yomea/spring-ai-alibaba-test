package com.demo;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallResponse;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolInterceptor;

/**
 * @author wuzhenhong
 * @date 2026/3/20 8:58
 */
public class ToolInterceptorTest extends ToolInterceptor {

    @Override
    public ToolCallResponse interceptToolCall(ToolCallRequest request, ToolCallHandler handler) {
        System.out.println("ToolInterceptorTest invoke start......................");
        ToolCallResponse response = handler.call(request);
        System.out.println("ToolInterceptorTest invoke end......................");
        return response;
    }

    @Override
    public String getName() {
        return "ToolInterceptorTest";
    }
}
