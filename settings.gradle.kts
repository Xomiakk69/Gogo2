pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("https://repo.maptiler.com")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.maptiler.com") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repo.maptiler.com") }
    }
}

rootProject.name = "BetaForAll"
include(":app")
