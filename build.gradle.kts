plugins {
    java
}

subprojects {
    apply<JavaPlugin>()

    repositories {
        mavenCentral()
    }
    
    dependencies {
        compileOnly("org.jetbrains:annotations:20.1.0")
    }
}
