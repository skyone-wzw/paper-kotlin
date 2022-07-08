import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm") version Versions.kotlin
    id("com.github.johnrengelman.shadow") version "7.1.2"
    `java-library`

    `maven-publish`
    signing
}

group = project.group
version = project.version

val isSnapshot = (project.version as String).contains("SNAPSHOT")

repositories {
    if (System.getenv("GRADLE_CHINA_MIRROR") == "true") {
        maven { url = URI("https://maven.aliyun.com/repository/public/") }
        mavenLocal()
    }
    maven { url = URI("https://papermc.io/repo/repository/maven-public/") }
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

    api("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}")
    api("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")

    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.coroutines}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.coroutines}")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:${Versions.serialization}")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:${Versions.serialization}")
    api("org.jetbrains.kotlinx:kotlinx-serialization-cbor-jvm:${Versions.serialization}")
    api("org.jetbrains.kotlinx:atomicfu-jvm:${Versions.atomicfu}")
    api("org.jetbrains.kotlinx:kotlinx-datetime-jvm:${Versions.datetime}")
}

tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.create<Jar>("javadocJar") {
    dependsOn.add(tasks.getByName("javadoc"))
    archiveClassifier.set("javadoc")
    from(tasks.getByName("javadoc"))
}

tasks.shadowJar {
    classifier = null
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

artifacts {
    archives(tasks.getByName("sourcesJar"))
    archives(tasks.getByName("javadocJar"))
    archives(tasks.shadowJar)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            pom {
                name.set(project.name)
                description.set("Kotlin language support library for plugin of paper minecraft server")
                url.set("https://github.com/skyone-wzw")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("skyone")
                        name.set("skyone-wzw")
                        email.set("skyone.wzw@qq.com")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:skyone-wzw/paper-kotlin.git")
                    developerConnection.set("scm:git@github.com:skyone-wzw/paper-kotlin.git")
                    url.set("https://github.com/skyone-wzw/paper-kotlin.git")
                }
            }

            artifact(tasks.getByName("sourcesJar"))
            artifact(tasks.getByName("javadocJar"))
            artifact(tasks.shadowJar)
        }
    }
    repositories {
        maven {
            setUrl(
                if (isSnapshot) "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                        else "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            )
            credentials {
                username = System.getenv("repo_name")
                password = System.getenv("repo_psd")
            }
        }
    }
}

//apply("secret/signing.gradle")