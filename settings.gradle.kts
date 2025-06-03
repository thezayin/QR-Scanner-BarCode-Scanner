pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "QR Scanner"
include(":app")
include(":scanner")
include(":common:values")
include(":common:framework")
include(":databases")
include(":generate")
include(":history")
include(":start-up")
