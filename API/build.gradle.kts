plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}
group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// (1) Selenium本体
    implementation("org.seleniumhq.selenium:selenium-java")

    // (2) WebDriverを自動でダウンロード・設定してくれるライブラリ
    // これがあれば、chromedriverの手動管理が不要になります
    implementation("io.github.bonigarcia:webdrivermanager:5.9.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
