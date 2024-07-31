plugins {
    alias(libs.plugins.self.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.sanmer.github"
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.square.retrofit)
    implementation(libs.square.retrofit.serialization)
    implementation(libs.square.okhttp)
    implementation(libs.square.okhttp.logging)
}
