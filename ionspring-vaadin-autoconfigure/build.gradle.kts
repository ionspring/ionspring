import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version ("2.1.0")
    id("java-library")
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly("com.vaadin:vaadin:${project.extra["vaadinVersion"]}")
    compileOnly("com.vaadin:vaadin-spring-boot-starter:${project.extra["vaadinVersion"]}")
    compileOnly("org.springframework.boot:spring-boot:${project.extra["springBootVersion"]}")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:${project.extra["springBootVersion"]}")
    compileOnly("org.springframework.boot:spring-boot-starter-security:${project.extra["springBootVersion"]}")
    compileOnly("com.github.mvysny.karibudsl:karibu-dsl:${project.extra["karibuVersion"]}")
    compileOnly("com.github.mvysny.karibudsl:karibu-dsl-v23:${project.extra["karibuVersion"]}")
    compileOnly("net.sf.jt400:jt400:${project.extra["jt400Version"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${project.extra["springBootVersion"]}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
