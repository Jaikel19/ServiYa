import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
}

kotlin {
  jvmToolchain(21)

  android {
    namespace = "com.example.seviya.compose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }

    androidResources { enable = true }
  }

  listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(project.dependencies.platform(libs.firebase.bom))
      implementation(libs.compose.uiToolingPreview)
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.android)
      implementation(libs.kotlinx.coroutines.android)
      implementation(libs.maps.compose)
      implementation(libs.play.services.location)
    }
    commonMain.dependencies {
      implementation(libs.gitlive.firebase.common)
      implementation(libs.gitlive.firebase.analytics)
      implementation(libs.gitlive.firebase.auth)
      implementation(libs.gitlive.firebase.firestore)
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.ui)
      implementation(libs.compose.components.resources)
      implementation(libs.compose.uiToolingPreview)
      implementation(libs.androidx.lifecycle.viewmodelCompose)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.compose)
      implementation(libs.koin.compose.viewmodel)
      implementation(libs.koin.compose.viewmodel.navigation)
      implementation(projects.shared)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.compose.material)
      implementation(libs.composeIcons.tabler)
      implementation(libs.compose.animation)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kamel.image.default)
    }
    commonTest.dependencies { implementation(libs.kotlin.test) }
  }
}

dependencies { androidRuntimeClasspath(libs.compose.uiTooling) }
