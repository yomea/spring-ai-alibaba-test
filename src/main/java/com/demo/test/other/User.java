package com.demo.test.other;

/**
 * @author wuzhenhong
 * @date 2026/3/26 9:05
 */
public record User(String name, Integer age) {

    public User() {
        this("");
        System.out.println(1111);
    }

    public User(String name) {
        this(name, 21);
        System.out.println(2222);
    }
    public User {
        System.out.println(name);
    }

    public String name() {
        return "xxxxxxxxx";
    }
}
