plugins {
    val kotlinVersion = "1.7.22"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "io.github.kloping"
version = "1.2.1"

repositories {
    maven("https://repo1.maven.org/maven2/")
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("net.mamoe:mirai-core")
    compileOnly("net.mamoe:mirai-console-compiler-common")
    implementation(platform("net.mamoe:mirai-bom:2.14.0"))
    testImplementation("net.mamoe:mirai-core-mock")
    testImplementation("net.mamoe:mirai-logging-slf4j")

    implementation(platform("org.slf4j:slf4j-parent:2.0.6"))
    testImplementation("org.slf4j:slf4j-simple")

    compileOnly("io.github.Kloping:SpringTool:0.5.8")
    compileOnly("io.github.Kloping:spt-web:0.2.0")
}

mirai {
    jvmTarget = JavaVersion.VERSION_11
}