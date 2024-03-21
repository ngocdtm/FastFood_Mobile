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
        maven {
            setUrl("https://jitpack.io")

        }
        maven {
            setUrl( "https://maven.aliyun.com/repository/public/")
        }
    }
}

rootProject.name = "FoodNhanh"
include(":app")
include("libs:picasso-master")
 