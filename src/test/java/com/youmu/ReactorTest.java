package com.youmu;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.time.Duration;

/**
 * @Author: YLBG-LDH-1506
 * @Description:
 * @Date: 2019/04/02
 */
public class ReactorTest {

    @Test
    public void test() {
        Flux<Object> objectFlux = Flux.just(1, 2, 3);
        objectFlux.doOnComplete(() -> {
            System.out.println("complete");
        }).subscribe(System.out::println).dispose();
        objectFlux.subscribe(System.out::println);
    }

    @Test
    public void test2() {
        Flux objectFlux = Flux.interval(Duration.ofSeconds(1));
        objectFlux.doOnComplete(() -> {
            System.out.println("complete");
        }).subscribe(System.out::println).dispose();
        while (true)
            ;
    }

    @Test
    public void test3() {
        Flux objectFlux = Flux.empty();
        objectFlux.doOnComplete(() -> {
            System.out.println("complete");
        }).subscribe(System.out::println).dispose();
    }

    @Test
    public void test4() {
        Flux objectFlux = Flux.error(() -> new IllegalArgumentException("内部异常"));
        objectFlux.doOnError(e -> System.out.println("异常" + e)).doOnComplete(() -> {
            System.out.println("complete");
        }).subscribe(System.out::println).dispose();
    }

    @Test
    public void test5() {
        Flux objectFlux = Flux.never();
        objectFlux.doOnError(e -> System.out.println("异常" + e)).doOnComplete(() -> {
            System.out.println("complete");
        }).subscribe(System.out::println).dispose();
    }

    @Test
    public void test6() {
        Flux objectFlux = Flux.range(1, 20);
        objectFlux.doOnError(e -> System.out.println("异常" + e)).doOnComplete(() -> {
            System.out.println("complete");
        }).subscribe(System.out::println).dispose();
    }

    @Test
    public void test7() {
        Flux objectFlux = Flux.range(1, 20);
        objectFlux.doOnError(e -> System.out.println("异常" + e)).doOnComplete(() -> {
            System.out.println("complete");
        }).subscribe(System.out::println).dispose();
    }
}
