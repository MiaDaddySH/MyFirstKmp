# Weather App

A Kotlin Multiplatform (KMP) sample application targeting Android, iOS, Desktop (JVM), and Web (Wasm/JS), with an optional Ktor server module. UI is built with Compose Multiplatform for cross-platform reuse, and networking is powered by Ktor Client with platform-specific engines.

Key features (demo)
- Cross-platform UI and navigation with Compose Multiplatform
- Weather search and 5-day forecast demo screens
- About screen

Note: The Ktor server module server is present but not included in the build by default (commented out in settings.gradle.kts). See “Optional: Enable and run the server” below.


## Tech Stack

- Kotlin Multiplatform (KMP)
- Compose Multiplatform (Android, iOS, Desktop, Web)
- Ktor Client (Android-OkHttp, iOS-Darwin, JVM-CIO, Wasm/JS engine)
- kotlinx-serialization, kotlinx-datetime
- Optional: Ktor Server (Netty)


## Project Structure
MyFirstKmp/
├── composeApp/        # Cross-platform UI app (Android, iOS bridge, Desktop, Web)
│   └── src/
│       ├── commonMain/    # Shared UI logic and screens (Compose)
│       ├── androidMain/   # Android-specific code
│       ├── iosMain/       # iOS-specific glue for KMP UI
│       ├── jvmMain/       # Desktop-specific code
│       └── wasmJsMain/    # Web (Wasm/JS) specific code
├── shared/            # Shared business and networking module (KMP)
│   └── src/
│       ├── commonMain/    # Ktor Client usage and shared logic
│       ├── androidMain/   # Platform adapters
│       ├── iosMain/       # iOS-specific code
│       ├── jvmMain/       # JVM-specific code
│       └── wasmJsMain/    # Wasm/JS-specific code
├── iosApp/            # Native iOS app wrapper (Xcode project)
└── server/            # Optional: Ktor server (not included by default)


---

## Prerequisites

- JDK 11 (the project targets Java 11)
- Android Studio (recommended for Android/Desktop/Web development)
- Xcode (required for iOS)
- macOS (commands below are for macOS)

---

## Getting Started

### Android
- Open the project in Android Studio, choose composeApp as the run target, and run.
- Or install to a connected device/emulator:

```bash
./gradlew :composeApp:installDebug
```

### iOS
- Open iosApp/iosApp.xcodeproj with Xcode.
- Choose a simulator or device and run. Xcode will invoke a Gradle task to embed the KMP framework automatically.

### Desktop (JVM)
- Run the desktop app:

```bash
./gradlew :composeApp:run
```

### Web (Wasm/JS)
- Start the development server (builds and opens in the browser):

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

---

## Optional: Enable and run the server (Ktor)

The server module exists but is disabled by default. To enable:

1) Edit settings.gradle.kts and uncomment:

```kotlin
include(":server")
```

2) Refresh/sync Gradle, then run the server:

```bash
./gradlew :server:run
```

Notes:
- The server depends on the shared module and includes content negotiation with JSON (kotlinx-serialization).
- Clients (shared/composeApp) use Ktor Client with platform-specific engines.

---

## Tests

- Run all tests:

```bash
./gradlew test
```

- Run server tests only:

```bash
./gradlew :server:test
```

---

## Code Pointers

- Cross-platform UI (Compose):
  - composeApp/src/commonMain/kotlin/com/example/myfirstkmp/
  - Example screens: WeatherScreen, AboutScreen
- Shared business/networking:
  - shared/src/commonMain/
  - Platform engines configured in respective xxxMain directories
- iOS entry/bridge:
  - iosApp/
- Optional server (Ktor):
  - server/src/main/kotlin/

---

## Tips

- Compose Multiplatform and hot reload plugins are configured to improve dev experience.
- iOS uses the Darwin engine (ktor-client-darwin) to address TLS compatibility.
- If Gradle sync/build issues occur, try a clean build:

```bash
./gradlew clean
```

---

# MyFirstKmp（中文）

一个使用 Kotlin Multiplatform（KMP）构建的多端应用示例，覆盖 Android、iOS、桌面（JVM）与 Web（Wasm/JS），并包含可选的 Ktor 服务端模块。UI 采用 Compose Multiplatform 进行跨端复用，网络层使用 Ktor Client（针对不同平台选择对应引擎）。

主要功能（示例）
- 基于 Compose Multiplatform 的跨平台界面与导航
- 天气查询与最近 7 天预报示例页面
- 关于页示例

说明：仓库中包含 Ktor 服务端模块 server，但默认未加入构建（在 settings.gradle.kts 中被注释）。如需启用，请参考下方「可选：启用并运行服务端」。

---

## 技术栈

- Kotlin Multiplatform（KMP）
- Compose Multiplatform（Android、iOS、桌面、Web）
- Ktor Client（Android-OkHttp、iOS-Darwin、JVM-CIO、Wasm/JS 引擎）
- kotlinx-serialization、kotlinx-datetime
- 可选：Ktor Server（Netty）

---

## 项目结构
MyFirstKmp/
├── composeApp/        # 跨平台 UI 应用（Android、iOS 桥接、Desktop、Web）
│   └── src/
│       ├── commonMain/    # 跨平台 UI 逻辑与界面（Compose）
│       ├── androidMain/   # Android 专属代码
│       ├── iosMain/       # iOS 专属桥接代码
│       ├── jvmMain/       # Desktop 专属代码
│       └── wasmJsMain/    # Web (Wasm/JS) 专属代码
├── shared/            # 共享业务与网络层模块（KMP）
│   └── src/
│       ├── commonMain/    # 共享业务/网络逻辑（Ktor Client 等）
│       ├── androidMain/   # 平台适配
│       ├── iosMain/       # iOS 专属代码
│       ├── jvmMain/       # JVM 专属代码
│       └── wasmJsMain/    # Wasm/JS 专属代码
├── iosApp/            # iOS 原生壳（Xcode 工程）
└── server/            # 可选：Ktor 服务端（默认未启用）


---

## 环境要求

- JDK 11（项目编译目标为 Java 11）
- Android Studio（推荐用于 Android/桌面/Web 开发与调试）
- Xcode（用于 iOS 运行与调试）
- macOS（以下命令均以 macOS 为例）

---

## 快速开始

### Android
- 使用 Android Studio 打开项目，选择 composeApp 作为运行目标并运行；
- 或使用 Gradle 命令安装到设备/模拟器：

```bash
./gradlew :composeApp:installDebug
```

### iOS
- 使用 Xcode 打开 iosApp/iosApp.xcodeproj；
- 选择模拟器或真机并运行。Xcode 会自动触发 Gradle 任务完成 KMP 框架的嵌入与签名。

### 桌面（JVM）
- 运行桌面应用：

```bash
./gradlew :composeApp:run
```

### Web（Wasm/JS）
- 启动开发服务器（会自动构建并在浏览器打开）：

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

---

## 可选：启用并运行服务端（Ktor）

项目包含 server 模块，但默认未启用。启用步骤：

1) 编辑 settings.gradle.kts，取消注释以下行：

```kotlin
include(":server")
```

2) 刷新/同步 Gradle 后，运行服务端：

```bash
./gradlew :server:run
```

说明：
- 服务端依赖 shared 模块，并已集成内容协商与 JSON（基于 kotlinx-serialization）。
- 客户端（shared/composeApp）使用 Ktor Client，并在不同平台使用相应引擎。

---

## 测试

- 运行全部测试：

```bash
./gradlew test
```

- 仅运行服务端测试：

```bash
./gradlew :server:test
```

---

## 代码位置指引

- 跨平台 UI（Compose）：
  - composeApp/src/commonMain/kotlin/com/example/myfirstkmp/
  - 示例页面：WeatherScreen、AboutScreen
- 共享业务与网络层：
  - shared/src/commonMain/
  - 各平台引擎适配位于对应的 xxxMain 目录
- iOS 入口与桥接：
  - iosApp/
- 可选服务端（Ktor）：
  - server/src/main/kotlin/

---

## 开发小贴士

- 已启用 Compose Multiplatform 及热重载相关插件以提升开发效率。
- iOS 端使用 Darwin 引擎（ktor-client-darwin）以提升 TLS 兼容性。
- 若遇到 Gradle 同步/构建异常，可先尝试清理构建缓存：

```bash
./gradlew clean
```