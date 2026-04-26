pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.PREFER_SETTINGS
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        // JetBrains Compose Multiplatform packages (navigation, viewmodel, lifecycle)
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "AdivinaRaza"

include(":core")
include(":data")
include(":usecases")
include(":app")
