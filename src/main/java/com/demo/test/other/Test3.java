package com.demo.test.other;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.Optional;

/**
 * @author wuzhenhong
 * @date 2026/3/19 10:36
 */
public class Test3 {

    public static void main(String[] args) throws GraphRunnerException {

        // 初始化 ChatModel
        DashScopeApi dashScopeApi = DashScopeApi.builder()
            .apiKey(System.getenv("API_KEY"))
            .build();

        ChatModel chatModel = DashScopeChatModel.builder()
            .dashScopeApi(dashScopeApi)
            .build();

        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
            .description("Get weather for a given city")
            .inputType(String.class)
            .build();

        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
            .name("weather_agent")
            .model(chatModel)
            .tools(weatherTool)
            .hooks(new AgentHookTest1())
            .interceptors(new ToolInterceptorTest())
            .systemPrompt("You are a helpful assistant")
            .saver(new MemorySaver())
            .build();

        // 运行 agent
        Optional<OverAllState> response = agent.invoke("what is the weather in San Francisco");
        if(response.isPresent()) {
            System.out.println(response.get().data());
        }
//        System.out.println(response.getText());

        /*Flux<NodeOutput> stream = agent.stream("what is the weather in San Francisco");
        stream.subscribe(
            output -> {
                // 检查是否为 StreamingOutput 类型
                if (output instanceof StreamingOutput streamingOutput) {
                    OutputType type = streamingOutput.getOutputType();

                    // 处理模型推理的流式输出
                    if (type == OutputType.AGENT_MODEL_STREAMING) {
                        // 流式增量内容，逐步显示
                        System.out.print(streamingOutput.message().getText());
                    } else if (type == OutputType.AGENT_MODEL_FINISHED) {
                        // 模型推理完成，可获取完整响应
                        System.out.println("\n模型输出完成");
                    }

                    // 处理工具调用完成（目前不支持 STREAMING）
                    if (type == OutputType.AGENT_TOOL_FINISHED) {
                        System.out.println("工具调用完成: " + output.node());
                    }

                    // 对于 Hook 节点，通常只关注完成事件（如果Hook没有有效输出可以忽略）
                    if (type == OutputType.AGENT_HOOK_FINISHED) {
                        System.out.println("Hook 执行完成: " + output.node());
                    }
                }
            },
            error -> System.err.println("错误: " + error),
            () -> System.out.println("Agent 执行完成")
        );*/
    }

}
