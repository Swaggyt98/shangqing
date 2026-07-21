# 检伤分类与辅助救治平台

本项目是一款面向应急救治、伤情评估和辅助处置场景的 Android 平板端应用，项目代号为 `SOLID`。应用可连接蓝牙低功耗（BLE）和 USB 串口生命体征设备，采集并展示伤员的心率、呼吸频率、无创血压、血氧饱和度和体温等数据。

平台结合 GCS、PHI、START 等评估方法与 TensorFlow Lite 本地模型推理，为伤员信息登记、检伤分类、救治记录、异常提醒和处置建议提供支持，并集成 MQTT、二维码扫描、语音识别及语音播报等能力。

> 本项目涉及医疗救治业务和个人健康数据。部署、测试和二次开发时，请遵守所在地区的隐私保护、数据安全和医疗软件相关要求，禁止将真实伤员数据、签名证书、服务密钥或生产环境凭据提交到代码仓库。

## 项目背景

应急救治现场通常需要同时完成伤员身份登记、生命体征采集、伤情评估、检伤分类和救治过程记录。传统的人工记录方式容易造成数据分散，也不利于连续监测、快速研判和后续追溯。

本项目用于在 Android 平板上统一承载检伤与辅助救治流程，主要目标包括：

- 接入便携式生命体征设备并集中展示监测数据。
- 通过 GCS、PHI 和 START 等方法辅助完成伤情评估与分级。
- 使用本地模型综合生命体征和评分数据，提供伤情分类参考。
- 记录伤员基本信息、伤情部位、救治措施和处置结果。
- 通过网络通信、二维码和语音能力提升现场信息采集与协同效率。

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 开发平台 | Android，横屏平板端应用 |
| 开发语言 | Java、Kotlin |
| 构建工具 | Gradle Wrapper、Android Gradle Plugin 8.4.0 |
| Kotlin | 1.9.0 |
| Android SDK | `compileSdk 34`、`minSdk 27`、`targetSdk 34` |
| 界面与架构 | AndroidX、Material Components、Navigation、View Binding |
| 网络与消息 | OkHttp、Volley、Paho MQTT |
| 数据解析 | Gson、Fastjson |
| 设备接入 | Android BLE、USB Serial for Android |
| 图表与信号处理 | MPAndroidChart、Waveform、IIRJ、Apache Commons Math |
| 智能能力 | TensorFlow Lite、百度千帆 SDK、腾讯 ASR |
| 图像与扫码 | Glide、PictureSelector、ZXing |

## 功能特性

### 伤员与救治业务

- 通过二维码扫描或手工录入维护伤员信息。
- 展示并管理伤员列表、当前伤员和关联设备。
- 记录受伤部位、伤情类型、伤情严重程度和综合情况。
- 记录护理、转运及其他救治处置过程。
- 汇总展示伤员生命体征、评分、伤情分类和救治结果。

### 检伤评估与辅助决策

- 支持格拉斯哥昏迷评分（GCS）交互与结果记录。
- 支持院前指数评分（PHI）计算与展示。
- 支持 START 检伤分类流程。
- 根据生命体征和评分数据计算并展示伤情严重程度。
- 使用 `model.tflite` 在设备端执行 TensorFlow Lite 推理。
- 可通过百度千帆服务生成辅助分析内容；相关结果仅供业务参考。

### 设备与生命体征

- 扫描、连接和管理 BLE 生命体征设备。
- 通过 USB 串口接收监护设备数据。
- 展示心率、呼吸频率、无创血压、血氧饱和度和体温等参数。
- 展示心电等波形，并提供血压分析和异常提醒能力。
- 通过 MQTT 发布、订阅设备及业务消息。

### 交互与工程能力

- 支持二维码扫描、图片选择和相机调用。
- 支持腾讯 ASR 语音识别和 Android TTS 中文语音播报。
- 使用 View Binding 管理界面控件。
- 提供基础单元测试和 Android 仪器测试目录。
- 使用本地配置文件隔离第三方服务凭据。

## 项目结构

```text
.
├── app
│   ├── libs                         # 腾讯 ASR 等本地 AAR/JAR 依赖
│   ├── src
│   │   ├── androidTest              # Android 仪器测试
│   │   ├── main
│   │   │   ├── java/com/zosoftware/solid
│   │   │   │   ├── api             # 大模型等外部服务调用
│   │   │   │   ├── bean            # 伤员、日志等数据对象
│   │   │   │   ├── ui              # 页面、业务 Fragment 与交互对话框
│   │   │   │   └── utils           # BLE、MQTT、推理、语音等工具
│   │   │   ├── res                 # 布局、图片、导航及其他 Android 资源
│   │   │   ├── assets
│   │   │   │   ├── model.tflite    # 本地推理模型
│   │   │   │   └── auth.properties.example
│   │   │   └── AndroidManifest.xml
│   │   └── test                     # JVM 单元测试
│   └── build.gradle
├── gradle
│   └── libs.versions.toml           # 版本目录
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── README.md
```

## 环境要求

- Android Studio
- JDK 17 或更高版本
- Android SDK Platform 34
- 支持 Android 8.1（API 27）或更高版本的设备
- 支持 BLE 的 Android 平板；USB 串口功能还需兼容的 USB 设备和连接线

项目已包含 Gradle Wrapper，无需单独安装 Gradle。

> 当前项目使用 Android Gradle Plugin 8.4.0，建议使用 JDK 17 构建。如果终端仍使用 Java 8，Gradle 配置阶段可能失败，请在 Android Studio 或 `JAVA_HOME` 中切换 JDK。

## 使用说明

### 1. 打开项目

使用 Android Studio 打开项目根目录，等待 Gradle Sync 和依赖下载完成。

首次构建前，请确认 `local.properties` 中已配置本机 Android SDK 路径：

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

### 2. 配置第三方服务

如需启用腾讯 ASR、百度千帆或其他联网服务，请复制示例配置：

```powershell
Copy-Item .\app\src\main\assets\auth.properties.example .\app\src\main\assets\auth.properties
```

然后在本地 `auth.properties` 中填写所需配置：

| 配置项 | 说明 |
| --- | --- |
| `TENCENT_ASR_APP_ID` | 腾讯云语音识别应用 ID |
| `TENCENT_ASR_SECRET_ID` | 腾讯云 Secret ID |
| `TENCENT_ASR_SECRET_KEY` | 腾讯云 Secret Key |
| `QIANFAN_ACCESS_KEY` | 百度千帆 Access Key |
| `QIANFAN_SECRET_KEY` | 百度千帆 Secret Key |
| `CHAT_ANYWHERE_API_URL` | 兼容接口地址 |
| `CHAT_ANYWHERE_API_KEY` | 兼容接口访问密钥 |

不使用相关在线能力时可将对应配置留空。`auth.properties` 仅用于本地开发，不应提交到 Git。

### 3. 构建调试版本

Windows：

```powershell
.\gradlew.bat :app:assembleDebug
```

Linux 或 macOS：

```bash
./gradlew :app:assembleDebug
```

构建产物默认位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

### 4. 运行测试

Windows：

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Linux 或 macOS：

```bash
./gradlew :app:testDebugUnitTest
```

连接 Android 设备或启动模拟器后，可运行仪器测试：

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

### 5. 安装与运行

可直接通过 Android Studio 选择目标设备运行，也可以在已连接设备上执行：

```powershell
.\gradlew.bat :app:installDebug
```

首次启动时，请根据系统提示授予蓝牙、附近设备、定位、相机和录音等权限。部分设备接入、二维码扫描和语音功能依赖对应硬件，模拟器无法完整验证这些能力。

## 设备与运行说明

- 应用界面固定为横屏，建议使用 Android 平板进行调试和部署。
- BLE 扫描依赖系统蓝牙及相关运行时权限；Android 12 及以上版本需要授予附近设备权限。
- USB 串口设备接入后，系统可能弹出访问授权提示，需由用户确认。
- MQTT、语音识别和大模型能力依赖网络及有效的服务配置。
- `model.tflite` 随应用打包，本地推理无需上传模型输入，但模型结果不应替代专业人员判断。

## 配置与安全建议

- 不要提交 `local.properties`、`auth.properties`、签名证书、API Key、访问令牌或 MQTT 密码。
- 不要在源码、日志、截图、测试数据或聊天记录中保留真实伤员信息和服务凭据。
- 正式部署时应使用独立签名证书，并通过安全的密钥管理方式注入生产配置。
- 联网服务应使用 HTTPS/TLS，并限制服务端凭据的权限、来源和有效期。
- 发布前应检查 Android 备份策略、明文流量配置、日志输出和运行时权限是否符合部署要求。
- 如凭据曾被提交到 Git 历史或其他公开位置，应立即在对应平台撤销并重新生成。
- 伤情分类、模型推理和自动生成内容仅可作为辅助信息，实际救治决策应由具备资质的专业人员作出。

## 许可证

本项目暂未声明开源许可证。在添加许可证之前，默认保留所有权利。如果计划公开开源，请根据项目归属和使用要求选择合适的许可证。
