import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    id("dev.hydraulic.conveyor") version "1.12"
}

version = "0.4.1"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    jvm {
//        withJava()
    }

    jvmToolchain(17)
    
//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "ComposeApp"
//            isStatic = true
//        }
//    }
    
    sourceSets {
        sourceSets.commonMain {
            kotlin.srcDir("build/generated/ksp/metadata")
        }

        val jvmMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.material3)
            implementation(compose.material)
//            implementation(libs.material.icons.extended)

            api(libs.koin.core)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.navigation.compose)

            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.kotlinx.datetime)

            api(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.kotlinx.serialization.core)
            implementation(libs.ktor.client.serialization)

            implementation(libs.docx4j)

            // Enables FileKit without Compose dependencies
            implementation(libs.filekit.core)

            // Enables FileKit with Composable utilities
            implementation(libs.filekit.compose)

            // import Kotlin API client BOM
            implementation(project.dependencies.platform(libs.openai.client.bom))

            // define dependencies without versions
            implementation(libs.openai.client)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(kotlin("test-annotations-common"))
            implementation(libs.assertk)

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.kotlinx.coroutines.core.jvm)
        }
    }
}

android {
    namespace = "dev.leobenz.pcplanner"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "dev.leobenz.pcplanner"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

val openaiApiKey: String = System.getenv("OPENAI_API_KEY") ?: "API_KEY_NOT_SET"

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev-leobenz-pcplanner"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
//    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
//    add("kspIosX64", libs.androidx.room.compiler)
//    add("kspIosArm64", libs.androidx.room.compiler)
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

//tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
//    if (name != "kspCommonMainKotlinMetadata" ) {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.uuid.ExperimentalUuidApi",
        )
    }
}

dependencies {
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
}

configurations.all {
    attributes {
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

kotlin.sourceSets["commonMain"].kotlin.srcDir("$buildDir/generated/src")

tasks.register("generateApiKey") {
    doLast {
        val dir = file("$buildDir/generated/src")
        dir.mkdirs()
        val file = file("$dir/ApiKey.kt")
        file.writeText("""
            package dev.leobenz.pcplanner
            
            const val OPENAI_API_KEY = "${System.getenv("OPENAI_API_KEY") ?: "API_KEY_NOT_SET"}"
        """.trimIndent())
    }
}

tasks.named("compileKotlinJvm") {
    dependsOn("generateApiKey")
}
