package com.example.enntetsu_digital_signage.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.HashMap;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

@Service
public class Bus_doko_access {

    // 系統番号の取得
    public String get_bus_number(HashMap<String, String> output, HashMap<String, String> classes, WebDriver driver) {
        String bus_number = "";
        WebElement input_element_bus_number = driver.findElement(By.cssSelector(classes.get("bus_number_main")));
        bus_number = input_element_bus_number.getText();
        output.put("bus_number", bus_number);
        return bus_number;
    }

    // 何個前のバス停かを取得
    public void get_previous(HashMap<String, String> output, HashMap<String, String> classes, WebDriver driver) {
        String input = "";
        List<WebElement> input_element_previous = driver.findElements(By.cssSelector(classes.get("between")));
        input = input_element_previous.get(0).getText();
        output.put("previous", input);
    }

    // 本来の出発時刻を取得
    public void get_departure(HashMap<String, String> output, HashMap<String, String> classes, WebDriver driver,
            String now, String bus_number, List<WebElement> input_element_buttons) {
        input_element_buttons.get(0).click();
        // 時刻を取得
        List<WebElement> input_element_time_schedule = driver.findElements(By.cssSelector(classes.get(
                "time_schedule")));
        // 系統番号を取得
        List<WebElement> input_element_bus_number_schedule = driver
                .findElements(By.cssSelector(classes.get("bus_number_schedule")));
        int now_hour = Integer.parseInt(now.substring(0, 2));
        int now_minute = Integer.parseInt(now.substring(3, 5));
        int now_score = now_hour * 60 + now_minute;
        for (int i = 0; i < input_element_bus_number_schedule.size(); i++) {
            if (input_element_bus_number_schedule.get(i).getText().equals(bus_number)) {
                String time_schedule = input_element_time_schedule.get(i * 2).getText();
                int hour = Integer.parseInt(time_schedule.substring(0, 2));
                int minute = Integer.parseInt(time_schedule.substring(3, 5));
                int score = hour * 60 + minute;
                if (now_score <= score) {
                    output.put("departure_time", time_schedule);
                    break;
                }
            }
        }
    }

    public HashMap<String, String> get_busdoko_json() {
        // URLを設定
        String url = "https://transfer-cloud.navitime.biz/entetsu/approachings?departure-busstop=00460589&arrival-busstop=00460001";
        // classの定義
        HashMap<String, String> classes = new HashMap<>();
        classes.put("bus_number_main", ".mx-4.mt-4.flex.justify-between"); // 系統番号
        classes.put("time_intermidiate_stop",
                ".flex.items-center.justify-center.rounded.border.border-button.bg-white.px-2.text-button.hover\\:no-underline.w-auto.h-10.text-base.grow"); // ボタン
        classes.put("between", ".mx-1.text-2xl"); // 途中バス停
        classes.put("time_schedule", "[class=\"text-[22px] font-bold\"]"); // 発車時刻・到着時刻
        classes.put("bus_number_schedule", "[class=\"font-bold\"]");// 発着時刻表での系統番号
        classes.put("intermidiate_stop_button",
                ".flex.h-full.min-w-\\[2\\.5rem\\].items-center.break-all.text-xs.text-link"); // 途中のバス停
        classes.put("board_number", ".w-\\[676px\\].space-y-4.px-6.py-4");// 系統・時刻表・のりば番号での取得
        classes.put("check_box", ".my-2.ml-0\\.5.mr-4.h-5.w-5.cursor-pointer.accent-link");// チェックボックス
        classes.put("bus_number_time_schedule", ".cursor-pointer.print\\:ml-0\\.5");// 系統番号情報
        classes.put("time_table", ".mt-6.w-full.table-fixed.border-collapse.border.border-dark-line");// 時刻表テーブル

        WebDriver driver = null;

        HashMap<String, String> output = new HashMap<>();
        output.put("delay", "");

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

            // 現在の時刻を取得
            DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime date_type_now = LocalDateTime.now();
            String now = date_type_now.format(time_formatter);

            // 最大で3秒待つように指定
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // ボタン周りの値の取得
            List<WebElement> input_element_buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(classes.get(
                            "time_intermidiate_stop"))));

            // 系統番号の取得と反映
            String bus_number = get_bus_number(output, classes, driver);

            // 何個前のバス停かを取得
            get_previous(output, classes, driver);

            // 本来の出発時刻を取得
            get_departure(output, classes, driver, now, bus_number, input_element_buttons);

            // 遅延時間を取得
            // 元のメイン画面へ戻る
            driver.get(url);

            input_element_buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get("time_intermidiate_stop"))));
            input_element_buttons.get(1).click();
            WebElement input_element_time_schedule_button = driver
                    .findElement(By.cssSelector(classes.get("intermidiate_stop_button")));
            input_element_time_schedule_button.click();
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                // TODO: handle exception
            }

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