# SOLID

`SOLID` is an Android tablet application for emergency care, triage, and assisted treatment workflows. It connects to BLE and USB serial vital-sign devices, displays patient data such as heart rate, respiration rate, blood pressure, SpO2, and body temperature, and combines GCS, PHI, and TensorFlow Lite inference to support injury classification, record management, and treatment assistance.

## Features

- Landscape Android tablet workspace for rescue and treatment scenarios
- BLE device scanning, connection, data reading, and device management
- USB serial device integration for vital-sign data such as NIBP, SpO2, HR, RR, and temperature
- User/patient information, care records, treatment workflow, and result pages
- GCS score, PHI score, START triage interactions, and injury severity display
- TensorFlow Lite model inference based on vital signs and scoring data
- MQTT messaging, QR code scanning, voice input/recognition, and text-to-speech support
- Local credential-based integration with Baidu Qianfan SDK and Tencent ASR services

## Tech Stack

- Android module: `app`
- Languages: Java, Kotlin
- Build system: Gradle / Android Gradle Plugin 8.4.0
- Kotlin: 1.9.0
- Android SDK: `compileSdk 34`, `minSdk 27`, `targetSdk 34`
- Application ID: `com.zosoftware.solid`
- Main dependencies: AndroidX, Material Components, OkHttp, Gson, Fastjson, MPAndroidChart, TensorFlow Lite, Paho MQTT, ZXing, Qianfan SDK, USB Serial, and local AAR/JAR dependencies under `app/libs`

## Project Structure

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
└── README.md
```

## Requirements

- Android Studio
- JDK 17 or later
- Android SDK Platform 34
- Gradle Wrapper, already included in this repository

> Note: This project uses Android Gradle Plugin 8.4.0. JDK 17 is recommended for builds. If your terminal still uses Java 8, Gradle configuration may fail. Configure JDK 17 in Android Studio or through `JAVA_HOME`.

## Local Configuration

Create or maintain `local.properties` on your machine for the Android SDK path and local-only settings:

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

If large-model, speech-recognition, MQTT, or other private services are enabled, keep real credentials in local secure configuration and do not commit them to Git. The current `.gitignore` excludes `local.properties`, signing certificates, and `app/src/main/assets/auth.properties`.

## Build

Open the project in Android Studio and wait for Gradle Sync to finish, or build from the terminal:

```powershell
.\gradlew.bat :app:assembleDebug
```

Run unit tests:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

## Security Notes

This repository should not contain real API keys, signing certificates, access tokens, MQTT passwords, or private local configuration. Keep secrets in local configuration files or a secure environment. If a secret has appeared in Git history, screenshots, or chat logs, revoke it and generate a new one on the corresponding platform.

Ignored sensitive or local files include:

- `local.properties`
- `sign.jks`
- `*.jks`, `*.keystore`, `*.p12`, `*.pem`
- `app/src/main/assets/auth.properties`
- Android Studio and Gradle build outputs
- Local archives and backup files

## Repository

Create an empty repository on GitHub, Gitee, or another Git hosting service, then add its remote URL:

```powershell
git remote add origin <your-repository-url>
git branch -M main
git push -u origin main
```
