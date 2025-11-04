package com.example.enntetsu_digital_signage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// APIの本体
@RestController
public class Bus_doko {

    // GETリクエスト処理
    @GetMapping("/bus_doko")
    public String hello(){
        return "hello";
    }
}
