package com.example.enntetsu_digital_signage.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDate;

@Service
public class Bus_doko_access {

    static final int WEEKDAY = 0;
    static final int SATUREDAY = 1;
    static final int SUNDAY = 2;

    static final int BUTTONS_MULTIPLE = 2;
    static final int DELAY_BUTTON = 1;

    // 曜日を返す関数
    public int get_day_of_the_week() {
        LocalDate today = LocalDate.now();
        int day_of_week = today.getDayOfWeek().getValue();
        if (day_of_week == 6) {
            return SATUREDAY;
        } else if (day_of_week == 7) {
            return SUNDAY;
        } else
            return WEEKDAY;
    }

    // 系統番号の取得
    public String get_bus_number(HashMap<String, String> output, HashMap<String, String> classes, WebDriverWait wait, int departure_number) {
        String bus_number = "";
        List<WebElement> input_element_bus_number = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get("bus_number_main"))));
        bus_number = input_element_bus_number.get(departure_number * BUTTONS_MULTIPLE).getText();
        output.put("bus_number", bus_number);
        return bus_number;
    }

    // 何個前のバス停かを取得
    public void get_previous(HashMap<String, String> output, HashMap<String, String> classes, WebDriver driver, int departure_number) {
        String input = "";
        List<WebElement> input_element_previous = driver.findElements(By.cssSelector(classes.get("previous")));
        input = input_element_previous.get(departure_number * BUTTONS_MULTIPLE).getText();
        output.put("previous", input);
    }

    // 本来の出発時刻を取得
    public void get_departure(HashMap<String, String> output, HashMap<String, String> classes,
            WebDriverWait wait,
            String now, String bus_number, int delay, int departure_number) {
        // ボタン周りの値の取得
        List<WebElement> input_element_buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(classes.get(
                        "time_intermidiate_stop"))));

        input_element_buttons.get(departure_number * BUTTONS_MULTIPLE).click();
        // 時刻を取得
        List<WebElement> input_element_time_schedule = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get(
                        "time_schedule"))));
        // 系統番号を取得
        List<WebElement> input_element_bus_number_schedule = wait
                .until(ExpectedConditions
                        .visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get("bus_number_schedule"))));
        // 遅延時間を差し引いて, 今の時刻を取得
        int now_hour = Integer.parseInt(now.substring(0, 2));
        int now_minute = Integer.parseInt(now.substring(3, 5));
        int now_score = now_hour * 60 + now_minute - delay;
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

    // 遅延時間を取得
    public int get_delay(HashMap<String, String> output, HashMap<String, String> classes, WebDriverWait wait,
            List<WebElement> input_element_buttons, String now, String bus_number, int day_of_week, int departure_number) {
        int delay = 0;
        input_element_buttons = wait.until(ExpectedConditions
                .visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get("time_intermidiate_stop"))));
        input_element_buttons.get(DELAY_BUTTON + BUTTONS_MULTIPLE * departure_number).click();
        WebElement input_element_time_schedule_button = wait
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(classes.get("intermidiate_stop_button"))));
        input_element_time_schedule_button.click();

        // 乗り場番号をクリック
        WebElement input_element_board_number = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(classes.get(
                        "board_number"))));
        List<WebElement> board_number_sapn = input_element_board_number.findElements(By.tagName("span"));
        for (WebElement span_element : board_number_sapn) {
            if (bus_number.equals(span_element.getText())) {
                span_element.click();
                break;
            }
        }
        // checkboxはすべて選択・解除があるので1つ最初に増える
        List<WebElement> input_element_checkboxes = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get("check_box"))));
        List<WebElement> input_element_bus_number_time_schedule = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get(
                        "bus_number_time_schedule"))));

        int size_of_bus_number = input_element_bus_number_time_schedule.size();
        if (!input_element_checkboxes.isEmpty()) {
            // 目的の系統番号以外をクリック
            for (int i = 0; i < size_of_bus_number; i++) {
                // 毎回再取得することで動作を安定化する
                input_element_bus_number_time_schedule = wait
                        .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(classes.get(
                                "bus_number_time_schedule"))));
                if (!bus_number.equals(input_element_bus_number_time_schedule.get(i).getText())) {
                    input_element_bus_number_time_schedule.get(i).click();
                    System.out.println(i);
                }
            }
        }
        // テーブルから情報を取得
        WebElement table = wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(classes.get("time_table"))));
        // tdタグで絞り込み
        List<WebElement> time_hour_and_minite = table.findElements(By.tagName("td"));
        // thタグで絞り込み
        List<WebElement> hour_table = table.findElements(By.tagName("th"));
        // 出力となるタイムテーブル
        HashMap<String, ArrayList<String>> time_schedule_table = new HashMap<>();
        // 曜日=>N時=>m分
        // div class="mx-1"...∧!display: noneで、i * 3 + day_of_weekを持ってくる
        for (int i = 0; i < time_hour_and_minite.size() / 3; i++) {
            // 登録するための仮置き
            ArrayList<String> table_temp = new ArrayList<>();
            // liタグで絞り込み
            List<WebElement> table_li_all = time_hour_and_minite.get(i * 3 + day_of_week)
                    .findElements(By.tagName("li"));
            // ArrayListに登録
            for (WebElement li : table_li_all) {
                if (!li.getText().equals(""))
                    table_temp.add(li.getText());
            }
            // iが時間(hour)を返してくれる
            String temp_hour = hour_table.get(i + 4).getText();
            // 登録
            time_schedule_table.put(temp_hour, table_temp);
        }
        // 後ろから探索して初めて今の時間前以降になるものを取得
        // hour_table.size()をi>=4まで回す
        int now_hour = Integer.parseInt(now.substring(0, 2));
        int now_minute = Integer.parseInt(now.substring(3, 5));
        int now_score = now_hour * 60 + now_minute;
        boolean f = false;
        for (int i = hour_table.size() - 1; i >= 4; i--) {
            String hour_str = hour_table.get(i).getText();
            ArrayList<String> minitue_table = time_schedule_table.get(hour_str);
            for (int j = minitue_table.size() - 1; j >= 0; j--) {
                String minite_str = minitue_table.get(j);
                int hour = Integer.parseInt(hour_str.substring(0, 2));
                int minute = Integer.parseInt(minite_str.substring(0, 2));
                int score = hour * 60 + minute;
                // 初めて過去の世界になったらおｋ
                if (now_score >= score) {
                    delay = now_score - score;
                    f = true;
                    break;
                }
            }
            if (f == true)
                break;
        }
        return delay;
    }

    public HashMap<String, String> get_busdoko_json(int departure_number) {
        // URLを設定
        String url = "https://transfer-cloud.navitime.biz/entetsu/approachings?departure-busstop=00460589&arrival-busstop=00460001";
        // classの定義
        HashMap<String, String> classes = new HashMap<>();
        classes.put("bus_number_main", ".mx-4.mt-4.flex.justify-between"); // 系統番号
        classes.put("time_intermidiate_stop",
                ".flex.items-center.justify-center.rounded.border.border-button.bg-white.px-2.text-button.hover\\:no-underline.w-auto.h-10.text-base.grow"); // ボタン
        classes.put("previous", ".mx-1.text-2xl"); // 途中バス停
        // classes.put("before_departure",
        // ".my-3.text-center.font-bold.text-error.text-lg"); // 始発駅発車前
        classes.put("time_schedule", "[class=\"text-[22px] font-bold\"]"); // 発車時刻・到着時刻
        classes.put("bus_number_schedule", "[class=\"font-bold\"]");// 発着時刻表での系統番号
        classes.put("intermidiate_stop_button",
                ".flex.h-full.min-w-\\[2\\.5rem\\].items-center.break-all.text-xs.text-link"); // 途中のバス停
        classes.put("board_number", ".h-full.table-fixed");// 系統・時刻表・のりば番号での取得
        classes.put("check_box", ".my-2.ml-0\\.5.mr-4.h-5.w-5.cursor-pointer.accent-link");// チェックボックス
        classes.put("hidden-checkbox", ".border-b.border-b-light-line.print:hidden"); // チェックボックスがオフである
        classes.put("bus_number_time_schedule", ".cursor-pointer.print\\:ml-0\\.5");// 系統番号情報
        classes.put("time_minite", "li:not([style='display: none;'])");// 時刻表テーブル
        classes.put("time_table", ".mt-6.w-full.table-fixed.border-collapse.border.border-dark-line");// 時刻表テーブル

        WebDriver driver = null;

        HashMap<String, String> output = new HashMap<>();

        try {
            // (1) WebDriverManagerが適切なバージョンのChromedriverを自動セットアップ
            WebDriverManager.chromedriver().setup();

            // (2) バックエンドで動かすため、ブラウザ画面を非表示 (headless) にする
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            // (3) Chromeドライバを起動
            driver = new ChromeDriver(options);

            // 最大で60秒待つように指定
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            // 正しく値が得られるまで実行する
            while (true) {
                // (4) 指定されたURLにアクセス
                driver.get(url);

                // 現在の時刻を取得
                DateTimeFormatter time_formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalDateTime date_type_now = LocalDateTime.now();
                String now = date_type_now.format(time_formatter);

                // 曜日を判定
                int day_of_week = get_day_of_the_week();

                // ボタン周りの値の取得
                List<WebElement> input_element_buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector(classes.get(
                                "time_intermidiate_stop"))));

                // 系統番号の取得と反映
                String bus_number = get_bus_number(output, classes, wait, departure_number);

                // 何個前のバス停かを取得
                // previousが存在しない場合は始発駅発車前
                try {
                    get_previous(output, classes, driver, departure_number);
                } catch (Exception e) {
                    output.put("previous", "始発駅発車前");
                }

                // 遅延時間を取得
                int delay = get_delay(output, classes, wait, input_element_buttons, now, bus_number, day_of_week, departure_number);

                // 遅延時間を登録
                output.put("delay", String.valueOf(delay));

                // 元のメイン画面へ戻る
                driver.get(url);

                // 系統番号が違ったらやり直す
                String test_bus_number = get_bus_number(output, classes, wait, departure_number);
                if (!bus_number.equals(test_bus_number)) {
                    continue;
                }
                // 本来の出発時刻を取得
                get_departure(output, classes, wait, now, bus_number, delay, departure_number);
                break;
            }

        } catch (Exception e) {
            e.printStackTrace(); // エラー処理 (実際にはもっと丁寧に行う)
            return output;
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return output;
    }
}