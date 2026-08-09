import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Properties

object VersionConfig {
    private val localDateTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
    private val baseVersionCode = 2
    private val baseVersionName = "0.0.2"

    val namespace = "top.fatweb.oxygen.toolbox"
    val applicationId = namespace
    val minSdk = 24
    val targetSdk = 36
    val versionCode = baseVersionCode
    val versionName = "$baseVersionName${
        if (baseVersionCode % 100 != 0) ".${
            localDateTime.format(
                DateTimeFormatter.ofPattern("yyMMdd")
            )
        }" else ""
    }"
}

val keystoreProperties = rootProject.file("keystore.properties").run {
    if (!exists()) {
        null
    } else {
        Properties().apply {
            load(FileInputStream(this@run))
        }
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
    alias(libs.plugins.hilt)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.room)
    alias(libs.plugins.aboutlibraries)
}

configure<ApplicationExtension> {
    namespace = VersionConfig.namespace
    compileSdk = VersionConfig.targetSdk

    defaultConfig {
        applicationId = VersionConfig.applicationId
        minSdk = VersionConfig.minSdk
        targetSdk = VersionConfig.targetSdk
        versionCode = VersionConfig.versionCode
        versionName = VersionConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        keystoreProperties?.let {
            create("release") {
                storeFile = rootProject.file(it["storeFile"] as String)
                storePassword = it["storePassword"] as String
                keyAlias = it["keyAlias"] as String
                keyPassword = it["keyPassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

base {
    archivesName.set("${VersionConfig.applicationId}-v${VersionConfig.versionName}_${VersionConfig.versionCode}")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
        languageVersion.set(KotlinVersion.KOTLIN_2_4)
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

aboutLibraries {
    android {
        registerAndroidTasks = false
    }
    export {
        outputFile = file("src/main/res/raw/dependencies.json")
    }
    library {
        exclusionPatterns = listOf<Regex>().map { it.toPattern() }
    }
}

afterEvaluate {
    tasks.findByName("preBuild")?.dependsOn(tasks.findByName("exportLibraryDefinitions")!!)
    tasks.findByName("kspDebugKotlin")?.dependsOn(tasks.findByName("generateDebugProto")!!)
    tasks.findByName("kspReleaseKotlin")?.dependsOn(tasks.findByName("generateReleaseProto")!!)
}

secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

ksp {
    arg("room.generateKotlin", "true")
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.espresso.core)

    ksp(libs.hilt.android)
    implementation(libs.hilt.android)
    androidTestImplementation(libs.hilt.android.testing)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    androidTestImplementation(libs.androidx.lifecycle.runtime.testing)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material.icons.core)
    implementation(libs.material.icons.extended)
    implementation(libs.material3.window.size)
    implementation(libs.animation.graphics)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    ksp(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.androidx.navigation.compose)
    androidTestImplementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.web.kit)
    implementation(libs.compose.webview)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    testImplementation(libs.paging.common)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)
    implementation(libs.androidsvg.aar)
    implementation(libs.timber)
    implementation(libs.compose.shimmer)
}