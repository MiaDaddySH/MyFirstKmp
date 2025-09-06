import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm()
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here

            // 添加内容协商和JSON序列化支持
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")

            // 添加HTTP客户端支持，用于调用天气API
            implementation("io.ktor:ktor-client-core:3.2.2")
            // implementation("io.ktor:ktor-client-cio:3.2.2")
            implementation("io.ktor:ktor-client-content-negotiation:3.2.2")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:3.2.2")
        }
        // iOS 使用 Darwin 引擎（关键改动，解决 TLS 问题）
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.2.2")
        }
                // JVM 使用 CIO 引擎
        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-cio:3.2.2")
        }
        // 可选：WASM/JS 平台使用 JS 引擎
        wasmJsMain.dependencies {
            implementation("io.ktor:ktor-client-js:3.2.2")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.myfirstkmp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
