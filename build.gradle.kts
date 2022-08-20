plugins {
    kotlin("multiplatform") version "1.7.10"
    id("maven-publish")
}

group = "dev.sitar"
version = "1.0"

publishing {
    repositories {
        mavenLocal()
    }
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm()

    js(BOTH) { nodejs() }

    linuxX64()
    mingwX64()
    macosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:atomicfu:0.18.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
                implementation(kotlin("test"))
            }
        }
    }
}
