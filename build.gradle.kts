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
    java
    `maven-publish`
    signing
    idea
    id("tech.yanand.maven-central-publish") version ("1.3.0")
}

group = "org.ionspring"
version = "0.1.1"

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "tech.yanand.maven-central-publish")

    extra["springBootVersion"] = "3.4.3"
    extra["jt400Version"] = "21.0.0"
    extra["vaadinVersion"] = "24.6.6"
    extra["karibuVersion"] = "2.3.2"
    extra["isReleaseVersion"] = true

    java.sourceCompatibility = JavaVersion.VERSION_17

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    val sourceJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks["javadoc"])
    }

    publishing {
        repositories {
            maven {
                url = uri(layout.buildDirectory.dir("staging-deploy"))
            }

        }
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifact(sourceJar.get())
                artifact(javadocJar.get())

                pom {
                    name.set(project.name)
                    description.set(project.name)
                    url.set("https://www.ionspring.org")
                    scm {
                        url.set("https://ionspring.github.com/ionspring")
                        connection.set("scm:git@github.com:ionspring/ionspring.git")
                        developerConnection.set("scm:git@github.com:ionspring/ionspring.git")
                    }
                    developers {
                        developer {
                            id.set("dferrand")
                            name.set("Damien Ferrand")
                            email.set("dferrand@gmail.com")
                        }
                    }
                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }

        }
    }
    signing {
        if (!project.version.toString().endsWith("-SNAPSHOT")) {
            sign(publishing.publications["mavenJava"])
        }
    }
    mavenCentral {
        authToken = findProperty("mavenCentralToken") as String?
        publishingType = "USER_MANAGED"
    }
}
