import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.vanniktech.maven.publish)
    alias(libs.plugins.dokka)
}

android {
    namespace = "top.cyclops.adapter"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.recyclerview)
    implementation(libs.androidx.paging.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
    coordinates("top.cyclops", "adapter-dsl", "1.0.0")
    pom {
        name = "adapter-dsl"
        description =
            "AdapterDsl uses a DSL to configure `RecyclerView` adapters, making the code more readable and maintainable. You can define item types, view bindings, and data payloads in a single, coherent block."
        url = "https://github.com/cyclops-top/adapter-dsl"
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://spdx.org/licenses/Apache-2.0.html"
            }
        }

        developers {
            developer {
                id = "cyclops-top/adapter-dsl"
                name = "Justin cheng"
                url = "https://www.cyclops.top"
            }
        }
        scm {
            url = "https://github.com/cyclops-top/adapter-dsl"
            connection = "scm:git:git@github.com:cyclops-top/adapter-dsl.git"
            developerConnection = "scm:git:ssh://git@github.com:cyclops-top/adapter-dsl.git"
        }
    }
}