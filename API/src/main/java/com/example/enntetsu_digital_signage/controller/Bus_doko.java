package com.example.enntetsu_digital_signage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// (1) さっき作ったServiceをインポート
import com.example.enntetsu_digital_signage.service.ScrapingService;

// APIの本体
@RestController
public class Bus_doko {

    // (2) Serviceを使えるように準備 (依存性の注入)
    @Autowired
    private ScrapingService scrapingService;

    // GETリクエスト処理
    @GetMapping("/bus_doko")
    public String hello() {
        return "hello";
    }
    
    @GetMapping("/scrape")
    public String scrapePage(@RequestParam(value = "url", defaultValue = "https://www.google.com") String url) {
        // (4) Serviceのメソッドを呼び出して結果を返す
        return scrapingService.getPageTitle(url);
    }
}
