package com.example.enntetsu_digital_signage.service;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.HashMap;

@Service
public class Bus_doko_access {
    public HashMap<String,String> get_busdoko_json() {
        // URLを設定
        String url = "https://transfer-cloud.navitime.biz/entetsu/approachings?departure-busstop=00460589&arrival-busstop=00460001";
        WebDriver driver = null;

        HashMap<String, String> output = new HashMap<>();
        output.put("bus_number", "");
        output.put("departure_time", "");
        output.put("delay", "");
        output.put("previous", "");

        try {
            // (1) WebDriverManagerが適切なバージョンのChromedriverを自動セットアップ
            WebDriverManager.chromedriver().setup();

            // (2) バックエンドで動かすため、ブラウザ画面を非表示 (headless) にする
            ChromeOptions options = new ChromeOptions();
            // options.addArguments("--headless"); // 画面なしで実行

            // (3) Chromeドライバを起動
            driver = new ChromeDriver(options);

            // (4) 指定されたURLにアクセス
            driver.get(url);

            // 系統番号の取得

            // 何個前のバス停かを取得

            // 本来の出発時刻を取得

            // 遅延時間を取得

        } catch (Exception e) {
            e.printStackTrace(); // エラー処理 (実際にはもっと丁寧に行う)
            return output;
        } finally {
            // (6) 必ずドライバを終了する
            if (driver != null) {
                driver.quit();
            }
        }

        return output;
    }
}