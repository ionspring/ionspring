import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * Copyright 2024 Damien Ferrand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
	id("java-library")
	kotlin("jvm") version "2.1.0"
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
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${project.extra["springBootVersion"]}")
	compileOnly("org.springframework.boot:spring-boot:${project.extra["springBootVersion"]}")
	compileOnly("org.springframework.boot:spring-boot-autoconfigure:${project.extra["springBootVersion"]}")
	compileOnly("org.springframework.boot:spring-boot-starter-security:${project.extra["springBootVersion"]}")
	compileOnly("org.springframework.boot:spring-boot-starter-data-jpa:${project.extra["springBootVersion"]}")
	compileOnly("net.sf.jt400:jt400:${project.extra["jt400Version"]}")
	compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
	testImplementation("net.sf.jt400:jt400:${project.extra["jt400Version"]}")
	testImplementation("org.springframework.boot:spring-boot-starter-test:${project.extra["springBootVersion"]}")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:${project.extra["springBootVersion"]}")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
