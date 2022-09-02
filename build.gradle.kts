plugins {
    kotlin("multiplatform") version "1.7.10"
    id("maven-publish")
    id("signing")
}

group = "dev.sitar"
version = "1.1.0"

val javadocJar = tasks.register("javadocJar", Jar::class.java) {
    archiveClassifier.set("javadoc")
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
                name.set("koi")
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
                        email.set("im@lostillusion.net")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("GPG_PRIVATE_KEY"),
        System.getenv("GPG_PRIVATE_PASSWORD")
    )
    sign(publishing.publications)
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
