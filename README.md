# CleanEditor v1.0.1

Android app in Kotlin and Jetpack Compose (Material 3).

## Build
The workflow `.github/workflows/android-build.yml` builds the debug APK on GitHub Actions with Java 17, Gradle 8.9 and Android SDK 34.

**Important:** The source files under `app/src/main` still need to be uploaded from the complete CleanEditor v1.0.1 source archive. The app will not build until those are added.

After the full source is uploaded, go to **Actions > Compilar APK Android > Run workflow**, and after a successful run download the artifact `CleanEditor-debug-apk`.
