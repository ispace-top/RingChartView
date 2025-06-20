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

// 发布配置
afterEvaluate { // 确保 Android Library 插件的任务已经创建
    publishing {
        publications {
            // 为 release 变体创建一个 MavenPublication
            create<MavenPublication>("maven") {
                groupId = "com.github.kerwin162" // 您的 GitHub 用户名
                artifactId = "RingChartView" // 您的库的 artifactId
                version = "1.0.0" // 您的库版本号，建议与您的 GitHub Release Tag 或 gradle.properties 中的版本号保持一致

                // 使用 Android Gradle Plugin 提供的组件来发布 AAR、源代码和 Javadoc
                from(components["release"])
            }
        }
        // **** 添加此 repositories 块 ****
        repositories {
            // 尽管 JitPack 会直接从 GitHub 拉取，但定义一个 Maven 仓库可以确保
            // Gradle 正确生成 publishMavenPublicationToMavenRepository 任务。
            // 这里的 URL 可以是任何有效的 Maven 仓库地址，即使它只是一个虚拟的。
            maven {
                name = "maven" // 仓库名称，与任务名称相关联
                url = uri("https://repo.maven.apache.org/maven2/") // 示例公共 Maven 仓库URL，JitPack会忽略此URL
            }
        }
    }
}

// 注意：这里移除了之前手动创建的 sourcesJar 和 javadocJar 任务，
// 因为 `from(components["release"])` 会自动处理它们。
// 如果您需要更复杂的文档生成（例如使用 Dokka），则需要单独配置 Dokka 插件。
