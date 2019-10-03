import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
}

group = "tv.fanart"
version = "0.0.1"

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
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}