package com.lxzh.basic.framework.modular.controller;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qun.zheng
 * @since 2021-03-25
 */
@RestController
@RequestMapping("/foo")
public class FooController {

    @GetMapping("one")
    public Foo getFoo() {
        return Foo.builder().date(new Date()).localDate(LocalDate.now()).localDateTime(LocalDateTime.now()).build();
    }

    @Data
    @Builder
    public static class Foo {
        private Date date;
        private LocalDate localDate;
        private LocalDateTime localDateTime;
    }
}
