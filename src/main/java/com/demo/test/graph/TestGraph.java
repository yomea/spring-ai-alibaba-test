package com.demo.test.graph;

import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.agent.exception.AgentException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.AppendStrategy;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;

import java.util.*;

import static com.alibaba.cloud.ai.graph.action.AsyncNodeActionWithConfig.node_async;

/**
 * 理解 Spring Ai Alibaba 的流程编排
 *
 * @author wuzhenhong
 * @date 2026/3/31 15:56
 */
public class TestGraph {

    public static void main(String[] args) throws GraphStateException {
        // 1. 定义全局状态
        OverAllState state = new OverAllState();
        state.registerKeyAndStrategy("topic", new ReplaceStrategy());
        state.input(Map.of("topic", "undefined"));

        // 2. 定义流程中的各个节点
        StateGraph stateGraph = new StateGraph("Research Workflow", () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();
            keyStrategyHashMap.put("messages", new AppendStrategy());
            return keyStrategyHashMap;
        })
            // 节点1：信息搜集节点，内部封装了调用搜索工具的LLM
            .addNode("research_node", node_async(((s, config) -> {
                System.out.println("research_node........");
                // 如果 Windows 中文乱码，设置 -Dfile.encoding=GBK 到启动参数中
                return Map.of("topic", "您好！hello world!");
            })))
            // 节点2：报告撰写节点，内部封装了调用文档工具的LLM
            .addNode("writing_node", node_async((s, config) -> {
                Object topic = s.data().getOrDefault("topic", "undefined");
                System.out.println(String.format("invoke LLM with topic %s........", topic));
                String javaStr = """
                    public class HelloWorld {
                                        
                        public static void main(String[] args) {
                            System.out.println("%s");
                        }
                                        
                    }
                    """.formatted(topic);
                List<Message> messageList = new ArrayList<>();
                AssistantMessage assistantMessage = AssistantMessage.builder()
                    .content(javaStr)
                    .build();
                messageList.add(assistantMessage);
                return Map.of("messages", messageList);
            }))
            // 节点3：结束节点
            .addNode("finish_node", node_async((s, config) -> {
                System.out.println("finish_node........");
                return Map.of();
            }))

            // 3. 编排节点间的执行路径
            .addEdge(StateGraph.START, "research_node")
            .addEdge("research_node", "writing_node")
            .addEdge("writing_node", "finish_node")
            .addEdge("finish_node", StateGraph.END);

        RunnableConfig config = RunnableConfig.builder()
            .threadId("testsssssssssss1111")
            .build();

        // 4. 执行工作流
        CompiledGraph compiledGraph = stateGraph.compile();
        AssistantMessage assistantMessage = extractAssistantMessage(compiledGraph.invoke(state, config));
        System.out.println(assistantMessage.getText());
    }

    private static AssistantMessage extractAssistantMessage(Optional<OverAllState> state) {
        return state.flatMap(s -> s.value("messages"))
            .stream()
            .flatMap(messageList -> ((List<?>) messageList).stream()
                .filter(msg -> msg instanceof AssistantMessage)
                .map(msg -> (AssistantMessage) msg))
            .reduce((first, second) -> second)
            .orElseThrow(() -> new AgentException("No AssistantMessage found in 'messages' state"));
    }

}
