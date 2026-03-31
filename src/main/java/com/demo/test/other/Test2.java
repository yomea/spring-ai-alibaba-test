package com.demo.test.other;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

/**
 * @author wuzhenhong
 * @date 2026/3/19 10:36
 */
public class Test2 {

    public static void main(String[] args) throws GraphRunnerException {

        // 初始化 ChatModel
        DashScopeApi dashScopeApi = DashScopeApi.builder()
            .apiKey(System.getenv("API_KEY"))
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
//            .baseUrl("https://dashscope.aliyuncs.com/api/v1")
            .completionsPath("/chat/completions")
            .build();

        DashScopeChatOptions defaultOptions = DashScopeChatOptions.builder()
            .model("qwen-plus")        // 指定模型 code
            .build();

        ChatModel chatModel = DashScopeChatModel.builder()
            .dashScopeApi(dashScopeApi)
            .defaultOptions(defaultOptions)
            .build();

        // 创建 Prompt
        Prompt prompt = new Prompt(new UserMessage("解释什么是微服务架构"));

// 调用并获取响应
        ChatResponse response = chatModel.call(prompt);
        String answer = response.getResult().getOutput().getText();
        System.out.println(answer);
        // 创建 agent
        /*ReactAgent agent = ReactAgent.builder()
            .name("java_agent")
            .model(chatModel)
            .systemPrompt("您是一个java专家")
            .saver(new MemorySaver())
            .build();

        // 运行 agent
        AssistantMessage response = agent.call("目前你了解到的最新java版本到了多少？");
        System.out.println(response.getText());*/

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
