plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.def.warlords.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.def.warlords.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    // TODO(#10): Migrate to `VERSION_11`, `VERSION_17` or `VERSION_21`.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src")
            manifest.srcFile("src/AndroidManifest.xml")
            assets.srcDir("../data")
        }
    }
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.material)
    implementation(project(":common"))
}
