package com.demo.test.other;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.AgentHook;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.HookPositions;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author wuzhenhong
 * @date 2026/3/26 10:11
 */
@HookPositions({HookPosition.BEFORE_AGENT, HookPosition.AFTER_AGENT})
public class AgentHookTest1 extends AgentHook {

    @Override
    public String getName() {
        return "AgentHookTest1";
    }

    @Override
    public CompletableFuture<Map<String, Object>> beforeAgent(OverAllState state, RunnableConfig config) {
        return CompletableFuture.completedFuture(Map.of("a", 1, "b", 2));
    }
}
