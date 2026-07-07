# SOLID

`SOLID` 是一款面向应急救治和伤情评估场景的 Android 平板端应用，也可理解为“检伤分类与辅助救治平台”。应用用于连接蓝牙低功耗和 USB 串口生命体征设备，采集并展示伤员的心率、呼吸率、血压、血氧、体温等数据，并结合 GCS、PHI 等评分和 TensorFlow Lite 模型辅助完成伤情分类、记录管理和救治建议。

## 功能特性

- 面向 Android 平板的横屏救治工作台界面
- 支持 BLE 设备扫描、连接、数据读取和设备管理
- 支持 USB 串口设备接入，展示血压、血氧、心率、呼吸率、体温等生命体征
- 提供用户/伤员信息、护理记录、救治流程、结果展示等页面
- 支持 GCS 评分、PHI 评分、START 分诊相关交互和伤情严重程度展示
- 集成 TensorFlow Lite 模型，用于基于生命体征和评分数据进行本地推理
- 集成 MQTT、二维码扫描、语音输入/识别、语音播报等能力
- 支持通过本地凭据接入百度千帆 SDK 与腾讯 ASR 语音识别服务

## 技术栈

- Android 应用模块：`app`
- 开发语言：Java、Kotlin
- 构建工具：Gradle / Android Gradle Plugin 8.4.0
- Kotlin：1.9.0
- Android SDK：`compileSdk 34`、`minSdk 27`、`targetSdk 34`
- 应用 ID：`com.zosoftware.solid`
- 主要依赖：AndroidX、Material Components、OkHttp、Gson、Fastjson、MPAndroidChart、TensorFlow Lite、Paho MQTT、ZXing、Qianfan SDK、USB Serial，以及 `app/libs` 目录下的本地 AAR/JAR 依赖

## 项目结构

```text
.
├── app/
│   ├── build.gradle
│   ├── libs/
│   │   ├── asr-file-recognize-release.aar
│   │   ├── asr-one-sentence-release.aar
│   │   ├── asr-realtime-release.aar
│   │   └── dsp-collection.jar
│   └── src/
│       ├── androidTest/
│       ├── main/
│       │   ├── java/com/zosoftware/solid/
│       │   ├── res/
│       │   └── assets/
│       └── test/
├── gradle/
├── build.gradle
├── gradle.properties
├── settings.gradle
└── README.zh-CN.md
```

## 环境要求

- Android Studio
- JDK 17 或更高版本
- Android SDK Platform 34
- 项目已包含 Gradle Wrapper

> 注意：当前项目使用 Android Gradle Plugin 8.4.0。建议使用 JDK 17 构建；如果终端仍在使用 Java 8，Gradle 配置阶段可能会失败。请在 Android Studio 或 `JAVA_HOME` 中配置 JDK 17。

## 本地配置

请在本机创建或维护 `local.properties`，用于保存 Android SDK 路径和仅本地使用的配置：

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

如果需要启用大模型、语音识别、MQTT 或其他私有服务，请把真实密钥放在本地安全配置中，不要提交到 Git 仓库。当前 `.gitignore` 已忽略 `local.properties`、签名证书和 `app/src/main/assets/auth.properties` 等敏感文件。

## 构建方式

推荐使用 Android Studio 打开项目并等待 Gradle Sync 完成，也可以在终端执行：

```powershell
.\gradlew.bat :app:assembleDebug
```

运行单元测试：

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

## 安全说明

仓库中不应包含真实 API Key、签名证书、访问令牌、MQTT 密码或本地私有配置。密钥请放在本地配置文件或安全环境中。如果某个密钥曾经出现在 Git 历史、截图或聊天记录里，请及时到对应平台作废并重新生成。

已忽略的敏感或本地文件包括：

- `local.properties`
- `sign.jks`
- `*.jks`、`*.keystore`、`*.p12`、`*.pem`
- `app/src/main/assets/auth.properties`
- Android Studio 与 Gradle 构建产物
- 本地压缩包和备份文件
