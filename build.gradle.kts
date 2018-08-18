import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`

    kotlin("jvm") version "1.2.60"
    kotlin("kapt") version "1.2.60"

    `java-gradle-plugin`
    `maven-publish`
}

group = "org.stvad"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.moshi:moshi:1.6.0")

    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.6.0")
    compile(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.2.60")
    compile("com.squareup", "kotlinpoet", "1.0.0-RC1")
    compile("com.amazon.alexa", "ask-sdk", "2.3.5")

    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

gradlePlugin {
    plugins {
        (plugins) {
            "kask"{
                id = "org.stvad.kask"
                implementationClass = "org.stvad.kask.gradle.KaskGeneratorGradlePlugin"
            }
        }
    }
}

publishing {
    repositories {
        maven(url = "build/repository")
    }
}