import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.50"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "tv.fanart"
version = "3.0.0"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    implementation("org.koin:koin-core:2.0.1")

    implementation("io.github.config4k:config4k:0.4.1")
    implementation("com.github.ajalt:clikt:2.2.0")

    implementation("com.squareup.retrofit2:retrofit:2.6.1")
    implementation("com.squareup.retrofit2:converter-gson:2.6.1")

    implementation("net.dv8tion:JDA:4.0.0_50")
    implementation("club.minnced:discord-webhooks:0.1.7")
}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = "tv.fanart.ApplicationKt"
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}