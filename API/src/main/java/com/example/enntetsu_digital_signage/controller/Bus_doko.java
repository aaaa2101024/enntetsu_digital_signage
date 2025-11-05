package com.example.enntetsu_digital_signage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// (1) さっき作ったServiceをインポート
import com.example.enntetsu_digital_signage.service.ScrapingService;
import com.example.enntetsu_digital_signage.service.Bus_doko_access;

import java.util.HashMap;

// APIの本体
@RestController
public class Bus_doko {

    // (2) Serviceを使えるように準備 (依存性の注入)
    @Autowired
    private ScrapingService scrapingService;
    @Autowired
    private Bus_doko_access bus_doko_access;

    // GETリクエスト処理
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
    
    @GetMapping("/scrape")
    public String scrapePage(@RequestParam(value = "url", defaultValue = "https://www.google.com") String url) {
        // (4) Serviceのメソッドを呼び出して結果を返す
        return scrapingService.getPageTitle(url);
    }

    @GetMapping("/get_busdoko")
    public HashMap<String, String> get_busdoko() {
        return bus_doko_access.get_busdoko_json();
    }
}
