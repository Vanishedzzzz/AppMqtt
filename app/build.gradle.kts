plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.example.mqttapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mqttapp"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Dependencias de MQTT
    implementation ("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")

    // Dependencias de compatibilidad de Android
    implementation ("androidx.appcompat:appcompat:1.6.1")  // Para compatibilidad con AppCompatActivity
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4") // Para usar layouts con constraints
    implementation ("com.google.android.material:material:1.9.0") // Para usar componentes Material Desi

}
