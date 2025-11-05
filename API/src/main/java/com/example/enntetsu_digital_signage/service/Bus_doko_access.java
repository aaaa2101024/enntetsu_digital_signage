package com.example.enntetsu_digital_signage.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;

@Service
class Bus_doko_access {
    public String getPageTitle(String url) {
        WebDriver driver = null;
        String title = "";

        try {
            // (1) WebDriverManagerが適切なバージョンのChromedriverを自動セットアップ
            WebDriverManager.chromedriver().setup();

            // (2) バックエンドで動かすため、ブラウザ画面を非表示 (headless) にする
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // 画面なしで実行

            // (3) Chromeドライバを起動
            driver = new ChromeDriver(options);

            // (4) 指定されたURLにアクセス
            driver.get(url);

            // (5) ページのタイトルを取得
            title = driver.getTitle();

        } catch (Exception e) {
            e.printStackTrace(); // エラー処理 (実際にはもっと丁寧に行う)
            return "Error: " + e.getMessage();
        } finally {
            // (6) 必ずドライバを終了する
            if (driver != null) {
                driver.quit();
            }
        }

        return title;
    }
}