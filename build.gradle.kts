plugins {
    kotlin("multiplatform") version "1.8.20"
    id("maven-publish")
    id("signing")
}

group = "dev.sitar"
version = "1.1.3"

val javadocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
}

// https://github.com/gradle/gradle/issues/26091
val signingTasks = tasks.withType<Sign>()
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(signingTasks)
}

val sonatypeUsername: String? = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")

publishing {
    publications {
        repositories {
            maven {
                name="oss"
                val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }

        withType<MavenPublication> {
            artifact(javadocJar)

            pom {
                name.set("kio")
                description.set("A Kotlin Multiplatform IO Library")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                url.set("https://github.com/lost-illusi0n/kio")

                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/lost-illusi0n/kio/issues")
                }

                scm {
                    connection.set("https://github.com/lost-illusi0n/kio.git")
                    url.set("https://github.com/lost-illusi0n/kio.git")
                }

                developers {
                    developer {
                        name.set("Marco Sitar")
                        email.set("marco+oss@sitar.dev")
                    }
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)

    explicitApi()

    jvm()

    js(IR) { nodejs() }

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
