package com.demo.test.other;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.HookPositions;
import com.alibaba.cloud.ai.graph.agent.hook.JumpTo;
import com.alibaba.cloud.ai.graph.agent.hook.ModelHook;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author wuzhenhong
 * @date 2026/3/26 14:44
 */
@HookPositions({HookPosition.BEFORE_MODEL, HookPosition.AFTER_MODEL})
public class ModelHookTest1 extends ModelHook {
    private boolean jump = false;

    @Override
    public CompletableFuture<Map<String, Object>> beforeModel(OverAllState state, RunnableConfig config) {
        return super.beforeModel(state, config);
    }

    @Override
    public CompletableFuture<Map<String, Object>> afterModel(OverAllState state, RunnableConfig config) {
        String key = "jump_to";
        if(!jump) {
            state.registerKeyAndStrategy(key, (o,n) -> n);
            return CompletableFuture.completedFuture(Map.of(key,JumpTo.tool));
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put(key, null);
            return CompletableFuture.completedFuture(map);
        }
    }

    @Override
    public String getName() {
        return "ModelHookTest1";
    }
}
