package com.demo.test.other;

import java.time.Duration;
import reactor.core.publisher.Flux;

/**
 * @author wuzhenhong
 * @date 2026/4/8 19:22
 */
public class FluxTest {

    public static void main(String[] args) throws Exception {

        Flux
            .range(0, 100)
//            .interval(Duration.ofSeconds(1))
//            .map(i -> "data: 数字 " + i + "\n\n")
//            .take(10)
            .delayElements(Duration.ofSeconds(1))
            .doOnNext(i -> System.out.println("data: 数字 " + i + "\n\n"))
            .doOnComplete(() -> System.out.println("完成！"))
//            .blockLast()
            .subscribe(System.out::println)
        ;

        System.out.println("tttttttttttt");
    }

}
