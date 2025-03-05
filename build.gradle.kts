// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("kotlin_version", "1.7.0")
        set("retrofit_version", "2.9.0")
        set("coroutines_version", "1.3.9")
        set("dagger_hilt_version", "2.42")
        set("version_name", "1.0.0.0-SNAPSHOT")
    }

    dependencies{
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.39.1")
    }

    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.application") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("com.android.library") version "7.2.1" apply false
}
