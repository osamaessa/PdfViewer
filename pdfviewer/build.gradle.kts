plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("maven-publish")

}

android {
    namespace = "com.osama.pdfviewer"
    compileSdk = 35

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release"){
                from(components["release"])
                groupId = "com.github.osamaessa"
                artifactId = "pdfviewer"
                version = "1.0"
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

//publishing {
//    publications {
//        create<MavenPublication>("release") {
//            from(components["release"])
//            groupId = "com.osama.pdfviewer"
//            artifactId = "pdfviewer"
//            version = "1.0.0"
//
//            pom {
//                name.set("PDF Viewer")
//                description.set("A lightweight PDF viewer for Android.")
//                url.set("https://github.com/osamaessa/PdfViewer") // Replace with your repo URL
//                licenses {
//                    license {
//                        name.set("Apache License 2.0")
//                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
//                    }
//                }
//                developers {
//                    developer {
//                        id.set("osama")
//                        name.set("Osama")
//                        email.set("osama.essa.azbaidy@gmail.com") // Replace with your email
//                    }
//                }
//                scm {
//                    connection.set("scm:git:git://github.com/osamaessa/PdfViewer.git")
//                    developerConnection.set("scm:git:ssh://github.com:osamaessa/PdfViewer.git")
//                    url.set("https://github.com/osamaessa/PdfViewer")
//                }
//            }
//        }
//    }
//}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}