pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // 如果RingChartCompose库本身依赖jitpack，则需要
    }
}

rootProject.name = "RingChartCompose"
include(":demo")
include(":lib")