import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `java-library`

    kotlin("jvm") version "1.2.60"
    kotlin("kapt") version "1.2.60"

    jacoco
    `maven-publish`
}

group = "org.stvad"
version = "0.1.0"

repositories {
    jcenter()
}

dependencies {
    implementation("com.squareup.moshi:moshi:1.6.0")

    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.6.0")
    compile(kotlin("stdlib-jdk8"))

    compile("com.squareup", "kotlinpoet", "1.0.0-RC1")
    compile("com.amazon.alexa", "ask-sdk", "2.3.5")
    compile("com.github.debop:koda-time:1.2.1")

    val arrowVersion = "0.7.3"
    compile("io.arrow-kt:arrow-core:$arrowVersion")
    compile("io.arrow-kt:arrow-syntax:$arrowVersion")
    compile("io.arrow-kt:arrow-typeclasses:$arrowVersion")
    compile("io.arrow-kt:arrow-data:$arrowVersion")
    compile("io.arrow-kt:arrow-instances-core:$arrowVersion")
    compile("io.arrow-kt:arrow-instances-data:$arrowVersion")
    kapt("io.arrow-kt:arrow-annotations-processor:$arrowVersion")

    testCompile("io.kotlintest:kotlintest-runner-junit5:3.1.9")
    testImplementation("io.mockk:mockk:1.8.6")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
    }

    val check by tasks
    check.dependsOn(this)
}