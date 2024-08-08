// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.aboutlibraries) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.parcelize) apply false
}