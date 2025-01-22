pluginManagement {
    repositories {
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
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol=true
            url = uri("http://210.99.223.38:8081/repository/maven-releases/")
            credentials {
                username = "admin"
                password = "!Admin@8081"
            }
        }
    }

}


rootProject.name = "Poen"
include(":app")
include(":bluetooth-module")
