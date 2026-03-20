package com.demo;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;

/**
 * @author wuzhenhong
 * @date 2026/3/20 8:57
 */
public class ModelInterceptorTest extends ModelInterceptor {

    @Override
    public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
        System.out.println("ModelInterceptorTest invoke start......................");
        ModelResponse response = handler.call(request);
        System.out.println("ModelInterceptorTest invoke end......................");
        return response;
    }

    @Override
    public String getName() {
        return "ModelInterceptorTest";
    }
}
