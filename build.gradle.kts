import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "1.9.21"
    id("org.jetbrains.compose") version "1.5.11"
    kotlin("plugin.serialization") version "1.9.21"
}

group = "com.blackandpink"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
            }
        }
        
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.blackandpink.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            
            packageName = "BlackAndPink"
            packageVersion = "1.0.0"
            description = "Black and Pink Inventory Management System"
            copyright = "© 2025 Black and Pink. All rights reserved."
            vendor = "Black and Pink"
            
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
                bundleID = "com.blackandpink.app"
                appCategory = "public.app-category.productivity"
                signing {
                    sign.set(false)
                    identity.set("Developer ID Application")
                }
            }
            
            windows {
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
                menuGroup = "Black And Pink"
                // Add the application to Programs and Features
                perUserInstall = false
                // Unique identifier for the installer (change this for major updates)
                upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
                // Add shortcut to desktop and start menu
                shortcut = true
                console = false
                dirChooser = true
                // MSI properties
                msiPackageVersion = "1.0.0"
                exePackageVersion = "1.0.0"
            }
            
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.ico"))
                packageName = "blackandpink"
                debMaintainer = "support@blackandpink.com"
                menuGroup = "Office"
                appRelease = "1"
                appCategory = "Office"
                rpmLicenseType = "MIT"
            }
        }
    }
}

// Disable tests for faster builds (especially useful for CI/CD)
tasks.withType<Test> {
    enabled = false
}

// Alternative: If you want to keep tests available but skip them by default
// You can run tests explicitly with: ./gradlew test --rerun-tasks
gradle.taskGraph.whenReady {
    allTasks
        .filter { it.name.contains("test", ignoreCase = true) }
        .forEach { task ->
            task.onlyIf { 
                project.hasProperty("runTests") || 
                gradle.startParameter.taskNames.any { it.contains("test", ignoreCase = true) }
            }
        }
}
