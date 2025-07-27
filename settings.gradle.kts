pluginManagement {
    repositories {
        maven{
            url = uri("https://nexus.cyclops.top/repository/maven-google/")
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven{
            url = uri("https://nexus.cyclops.top/repository/maven-central/")
        }
        maven{
            url = uri("https://nexus.cyclops.top/repository/gradle-plugin/")
        }

        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven{
            url = uri("https://nexus.cyclops.top/repository/maven-central/")
        }
        maven{
            url = uri("https://nexus.cyclops.top/repository/maven-google/")
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "AdapterDsl"
include(":app")
include(":libs")
