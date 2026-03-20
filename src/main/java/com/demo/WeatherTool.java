package com.demo;

import java.util.function.BiFunction;
import org.springframework.ai.chat.model.ToolContext;

public class WeatherTool implements BiFunction<String, ToolContext, String> {
    @Override
    public String apply(String city, ToolContext toolContext) {
        return "It's always sunny in " + city + "!";
    }
}