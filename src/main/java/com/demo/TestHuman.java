package com.demo;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata.ToolFeedback;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.hip.HumanInTheLoopHook;
import com.alibaba.cloud.ai.graph.agent.hook.hip.ToolConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import java.util.List;
import java.util.Optional;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

/**
 * @author wuzhenhong
 * @date 2026/3/19 10:36
 */
public class TestHuman {

    public static void main(String[] args) throws GraphRunnerException {

        // 初始化 ChatModel
        DashScopeApi dashScopeApi = DashScopeApi.builder()
            .apiKey(System.getenv("API_KEY"))
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .completionsPath("/chat/completions")
            .build();

        DashScopeChatOptions defaultOptions = DashScopeChatOptions.builder()
            .model("qwen-plus")        // 指定模型 code
            .temperature(0.7)          // 可选参数
            .build();

        ChatModel chatModel = DashScopeChatModel.builder()
            .dashScopeApi(dashScopeApi)
            .defaultOptions(defaultOptions)
            .build();

        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
            .description("Get weather for a given city")
            .inputType(String.class)
            .build();

        HumanInTheLoopHook humanInTheLoopHook = HumanInTheLoopHook.builder()
            .approvalOn("get_weather", ToolConfig.builder()
                .description("天气请求审批！")
                .build())
            .build();

        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
            .name("weather_agent")
            .model(chatModel)
            .tools(weatherTool)
            .hooks(humanInTheLoopHook)
            .interceptors(new ModelInterceptorTest(), new ToolInterceptorTest())
            .systemPrompt("You are a helpful assistant")
            .saver(new MemorySaver())
            .build();

        String threadId = "user-session-001";
        RunnableConfig config = RunnableConfig.builder()
            .threadId(threadId)
            .build();

        // 运行 agent
        Optional<NodeOutput> result = agent.invokeAndGetOutput("what is the weather in San Francisco", config);

        // 检查中断并处理
        if (result.isPresent() && result.get() instanceof InterruptionMetadata) {
            InterruptionMetadata interruptionMetadata = (InterruptionMetadata) result.get();

            System.out.println("检测到中断，需要人工审批");
            List<ToolFeedback> toolFeedbacks =
                interruptionMetadata.toolFeedbacks();

            for (InterruptionMetadata.ToolFeedback feedback : toolFeedbacks) {
                System.out.println("工具: " + feedback.getName());
                System.out.println("参数: " + feedback.getArguments());
                System.out.println("描述: " + feedback.getDescription());
            }

            // 构建批准反馈
            InterruptionMetadata.Builder feedbackBuilder = InterruptionMetadata.builder()
                .nodeId(interruptionMetadata.node())
                .state(interruptionMetadata.state());

            // 对每个工具调用设置批准决策
            interruptionMetadata.toolFeedbacks().forEach(toolFeedback -> {
                InterruptionMetadata.ToolFeedback approvedFeedback =
                    InterruptionMetadata.ToolFeedback.builder(toolFeedback)
                        .result(InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED)
                        .build();
                feedbackBuilder.addToolFeedback(approvedFeedback);
            });

            InterruptionMetadata approvalMetadata = feedbackBuilder.build();

            // 使用批准决策恢复执行
            RunnableConfig resumeConfig = RunnableConfig.builder()
                .threadId(threadId) // 相同的线程ID以恢复暂停的对话
                .addMetadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY, approvalMetadata)
                .build();

            // 第二次调用以恢复执行
            System.out.println("\n=== 第二次调用：使用批准决策恢复 ===");
            Optional<NodeOutput> finalResult = agent.invokeAndGetOutput("", resumeConfig);

            if (finalResult.isPresent()) {
                System.out.println("执行完成");
            }
        }
    }

}
