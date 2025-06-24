plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish") // 添加这一行，用于发布到 Maven 仓库
}

android {
    namespace = "top.itjl.ringchart"
    compileSdk = 35

    defaultConfig {
        minSdk = 21 // 保持当前值，如果您的库或其依赖需要更高的 API 级别，您可能需要调整
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4" // 与您的 Kotlin 版本兼容
    }
    // 明确配置 singleVariant 用于发布，这有助于 AGP 注册相应的 SoftwareComponent
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.06.00")) // Compose BOM
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // 如果需要 Material Design 组件
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    // implementation("androidx.xr.compose:compose-testing:1.0.0-alpha04") // 之前已建议移除此依赖

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// 发布配置 - 放在 afterEvaluate 块中以确保所有组件都已注册
afterEvaluate {
    publishing {
        publications {
            // 为 release 变体创建一个 MavenPublication
            create<MavenPublication>("maven") {
                groupId = "com.github.kerwin162" // 您的 GitHub 用户名
                artifactId = "RingChartView" // 您的库的 artifactId
                version = "1.0.0" // 您的库版本号，建议与您的 GitHub Release Tag 或 gradle.properties 中的版本号保持一致

                // 使用 Android Gradle Plugin 提供的组件来发布 AAR、源代码和 Javadoc
                // components["release"] 应该在 afterEvaluate 块中可用
                from(components["release"])
            }
        }
        // 定义仓库，这个 repositories 块也应该在 publishing 块内部，与 publications 并列
        repositories {
            maven {
                name = "maven" // 仓库名称，与任务名称相关联
                // 对于 JitPack，这里的 URL 不重要，但需要一个有效的 Maven URL 来让 Gradle 生成任务
                url = uri("https://repo.maven.apache.org/maven2/")
            }
        }
    }
}
