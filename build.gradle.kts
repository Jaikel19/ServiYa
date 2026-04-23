plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.composeMultiplatform) apply false
  alias(libs.plugins.composeCompiler) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.androidMultiplatformLibrary) apply false
  alias(libs.plugins.androidLint) apply false
  alias(libs.plugins.google.services) apply false
  alias(libs.plugins.kotlinSerialization) apply false
  alias(libs.plugins.spotless)
}

spotless {
  ratchetFrom("origin/main")

  kotlin {
    target("**/src/**/*.kt")
    targetExclude("**/build/**", "**/generated/**")
    ktfmt()
  }

  kotlinGradle {
    target("*.gradle.kts", "**/*.gradle.kts")
    targetExclude("**/build/**")
    ktfmt()
  }

  format("misc") {
    target(".prettierrc.yml", "**/*.yaml", "**/*.yml", "**/*.json")
    targetExclude("**/build/**", "**/.gradle/**")

    trimTrailingWhitespace()
    leadingTabsToSpaces(2)
    endWithNewline()
    prettier(mapOf("prettier" to "3.8.1"))
        .configFile(rootProject.file(".prettierrc.yml"))
        .npmInstallCache("${rootProject.rootDir}/.gradle/spotless-npm-cache")
  }
}
