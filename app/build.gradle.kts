plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "xmnh.soulfrog"
    compileSdk = 37

    defaultConfig {
        applicationId = "xmnh.soulfrog"
        minSdk = 29
        targetSdk = 37
        versionCode = 206
        versionName = "2.0.6"

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += "META-INF/**"
            pickFirsts += "META-INF/xposed/*"
        }
    }
}
androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.outputs.forEach { output ->
            output.outputFileName.set(
                "SoulFrog_${variant.name}_${output.versionName.get()}.apk"
            )
        }
    }
}
dependencies {
    compileOnly(libs.libxposedApi)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
}