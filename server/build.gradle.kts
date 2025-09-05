plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.example.myfirstkmp"
version = "1.0.0"
application {
    mainClass.set("com.example.myfirstkmp.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)

    // 添加内容协商和JSON序列化支持
    implementation("io.ktor:ktor-server-content-negotiation:3.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")
    
    // 添加HTTP客户端支持，用于调用天气API
    implementation("io.ktor:ktor-client-core:3.2.2")
    implementation("io.ktor:ktor-client-cio:3.2.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.2")

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}