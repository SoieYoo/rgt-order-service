package com.example.springjwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String mainP() {
        return "[RGT 사전 과제] 테이블 주문 플랫폼 서비스 API";
    }
}
